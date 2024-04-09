package repository;

import constant.Status;
import constant.TaskType;
import exception.DuplicateTaskIdException;
import exception.MissingEpicException;
import exception.TimeOverlapException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static constant.Constants.DEFAULT_NULL_TASK_END_TIME;
import static constant.Constants.DEFAULT_NULL_TASK_START_TIME;
import static constant.Constants.DEFAULT_TASK_DURATION_IN_MINUTES;
import static constant.Constants.DUPLICATE_EPIC_ID;
import static constant.Constants.DUPLICATE_SUBTASK_ID;
import static constant.Constants.DUPLICATE_TASK_ID;

public class AllTasksRepository {
    HashMap<Integer, Task> idToTaskMap;
    HashMap<Integer, Epic> idToEpicMap;
    HashMap<Integer, Subtask> idToSubTaskMap;
    private TreeSet<Task> prioritizedTasksByStartTime;

    public AllTasksRepository() {
        this.idToTaskMap = new HashMap<>();
        this.idToEpicMap = new HashMap<>();
        this.idToSubTaskMap = new HashMap<>();
        this.prioritizedTasksByStartTime = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() == DEFAULT_NULL_TASK_START_TIME && task2.getStartTime() == DEFAULT_NULL_TASK_START_TIME) {
                return Integer.compare(task1.getId(), task2.getId());
            }

            if (task1.getStartTime() == DEFAULT_NULL_TASK_START_TIME) {
                return 1;
            }

            if (task2.getStartTime() == DEFAULT_NULL_TASK_START_TIME) {
                return -1;
            }
            return task1.getStartTime().compareTo(task2.getStartTime());
        });
    }

    public void addTask(Task task) throws TimeOverlapException {
        checkForTimeOverlap(task);
        if (task.getClass().getSimpleName().equals(TaskType.TASK.getTaskType())) {
            addTask(task.getId(), task);
        } else if (task.getClass().getSimpleName().equals(TaskType.SUBTASK.getTaskType())) {
            addSubtask(task.getId(), (Subtask) task);
        } else if (task.getClass().getSimpleName().equals(TaskType.EPIC.getTaskType())) {
            addEpic(task.getId(), (Epic) task);
        }
    }

    public void updateTask(Task task) throws TimeOverlapException {
        checkForTimeOverlap(task);
        if (task.getClass().getSimpleName().equals(TaskType.TASK.getTaskType())) {
            updateTask(task.getId(), task);
        } else if (task.getClass().getSimpleName().equals(TaskType.SUBTASK.getTaskType())) {
            updateSubtask(task.getId(), (Subtask) task);
        } else if (task.getClass().getSimpleName().equals(TaskType.EPIC.getTaskType())) {
            updateEpic(task.getId(), (Epic) task);
        }
    }

    private void updateTask(int id, Task task) {
        if (isTaskExisted(task)) {
            Task oldTask = idToTaskMap.get(id);
            removeFromPrioritizedTasks(oldTask);
            idToTaskMap.put(id, task);
            addToPrioritizedTasks(task);
        }
    }

    private void updateSubtask(int id, Subtask subtask) {
        if (isSubtaskExisted(subtask) && idToSubTaskMap.get(id).getEpicId() == subtask.getEpicId()) {
            Subtask oldSubtask = idToSubTaskMap.get(id);
            removeFromPrioritizedTasks(oldSubtask);
            idToSubTaskMap.put(id, subtask);
            addToPrioritizedTasks(subtask);
            int epicId = subtask.getEpicId();
            changeEpicStatusAndTimeProperties(epicId);
        }
    }

    private void updateEpic(int id, Epic epic) {
        if (isEpicExisted(epic)) {
            idToEpicMap.get(id).setName(epic.getName());
            idToEpicMap.get(id).setDescription(epic.getDescription());
        }
    }

    public boolean isEpicExisted(Epic epic) {
        return idToEpicMap.containsKey(epic.getId());
    }

    public boolean isTaskExisted(Task task) {
        return idToTaskMap.containsKey(task.getId());
    }

    public boolean isEpicExisted(int id) {
        return idToEpicMap.containsKey(id);
    }

    public boolean isSubtaskExisted(Subtask subtask) {
        return idToSubTaskMap.containsKey(subtask.getId());
    }

    public Status calculateNewEpicStatus(Epic epic) {

        if (epic.getSubtaskIdList().isEmpty()) {
            return Status.NEW;
        }

        boolean isNewTask = false;
        boolean isDoneTask = false;

        for (int idSubtask : epic.getSubtaskIdList()) {
            Status status = idToSubTaskMap.get(idSubtask).getStatus();
            if (status == Status.NEW) {
                isNewTask = true;
            }
            if (status == Status.DONE) {
                isDoneTask = true;
            }
            if (isNewTask && isDoneTask) {
                return Status.IN_PROGRESS;
            }
        }
        if (isNewTask) {
            return Status.NEW;
        }
        if (isDoneTask) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    public void calculateAndSetNewEpicTimeProperties(Epic epic) {
        List<Integer> subtasksId = epic.getSubtaskIdList();

        if (subtasksId.isEmpty()) {
            setNewTimeProperties(epic, DEFAULT_NULL_TASK_START_TIME, DEFAULT_TASK_DURATION_IN_MINUTES, DEFAULT_NULL_TASK_END_TIME);
            return;
        }

        LocalDateTime minStartTime = DEFAULT_NULL_TASK_START_TIME;
        LocalDateTime maxEndTime = DEFAULT_NULL_TASK_END_TIME;
        int totalDurationInMinutes = 0;

        for (int subtaskId : subtasksId) {
            Subtask currentSubtask = idToSubTaskMap.get(subtaskId);
            if (currentSubtask == null) {
                continue;
            }

            minStartTime = getMinStartTime(minStartTime, currentSubtask.getStartTime());
            maxEndTime = getMaxEndTime(maxEndTime, currentSubtask.getEndTime());

            totalDurationInMinutes += currentSubtask.getDurationInMinutes();
        }

        setNewTimeProperties(epic, minStartTime, totalDurationInMinutes, maxEndTime);
    }

    public Task getTaskById(int id) {
        if (idToTaskMap.containsKey(id)) {
            return idToTaskMap.get(id);
        }

        if (idToSubTaskMap.containsKey(id)) {
            return idToSubTaskMap.get(id);
        }

        if (idToEpicMap.containsKey(id)) {
            return idToEpicMap.get(id);
        }
        return null;
    }

    public List<Task> getAllTaskType() {
        return new ArrayList<>(idToTaskMap.values());
    }

    public List<Task> getAllSubtaskType() {
        return new ArrayList<>(idToSubTaskMap.values());
    }

    public List<Task> getAllEpicType() {
        return new ArrayList<>(idToEpicMap.values());
    }

    public List<Task> getAllTasks() {
        List<Task> resultAllTaskList = new ArrayList<>();

        resultAllTaskList.addAll(idToTaskMap.values());
        resultAllTaskList.addAll(idToSubTaskMap.values());
        resultAllTaskList.addAll(idToEpicMap.values());
        return resultAllTaskList;
    }

    public List<Task> getAllEpicSubtasks(Epic epic) {
        List<Task> resultEpicSubtaskList = new ArrayList<>();
        for (int idSubtask : epic.getSubtaskIdList()) {
            resultEpicSubtaskList.add(idToSubTaskMap.get(idSubtask));
        }
        return resultEpicSubtaskList;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasksByStartTime);
    }

    public List<Task> deleteTaskById(int id) {
        List<Task> deletedTasks = new ArrayList<>();

        if (idToTaskMap.containsKey(id)) {
            Task deletedTask = idToTaskMap.remove(id);
            removeFromPrioritizedTasks(deletedTask);
            deletedTasks.add(deletedTask);
        }

        if (idToSubTaskMap.containsKey(id)) {
            Subtask deletedTask = idToSubTaskMap.remove(id);
            int epicId = idToSubTaskMap.get(id).getEpicId();
            Epic epic = idToEpicMap.get(epicId);

            removeFromPrioritizedTasks(deletedTask);
            deleteSubtaskInsideEpic(epic, id);
            Status newEpicStatus = calculateNewEpicStatus(epic);
            epic.setStatus(newEpicStatus);
            calculateAndSetNewEpicTimeProperties(epic);

            deletedTasks.add(idToSubTaskMap.remove(id));
        }

        if (idToEpicMap.containsKey(id)) {
            deletedTasks.add(idToEpicMap.get(id));
            deletedTasks.addAll(deleteSubtaskConnectedWithEpic(idToEpicMap.get(id)));
            idToEpicMap.remove(id);
        }
        return deletedTasks;
    }

    public List<Task> deleteAllTaskType() {
        List<Task> deletedTasks = new ArrayList<>(idToTaskMap.values());
        idToTaskMap.clear();

        prioritizedTasksByStartTime.removeAll(deletedTasks);
        return deletedTasks;
    }

    public List<Task> deleteAllSubtaskType() {
        List<Task> deletedTasks = new ArrayList<>(idToSubTaskMap.values());
        idToSubTaskMap.clear();
        deleteAllSubTaskInsideAllEpic();

        prioritizedTasksByStartTime.removeAll(deletedTasks);
        return deletedTasks;
    }

    public List<Task> deleteAllEpicType() {
        List<Task> deletedEpics = new ArrayList<>(idToEpicMap.values());
        List<Task> deletedSubtasks = new ArrayList<>(idToSubTaskMap.values());
        idToSubTaskMap.clear();
        idToEpicMap.clear();

        prioritizedTasksByStartTime.removeAll(deletedSubtasks);
        List<Task> allDeletedTasks = new ArrayList<>();
        allDeletedTasks.addAll(deletedEpics);
        allDeletedTasks.addAll(deletedSubtasks);
        return allDeletedTasks;
    }

    public List<Task> deleteAllTasks() {
        List<Task> deletedTasks = getAllTasks();

        idToTaskMap.clear();
        idToSubTaskMap.clear();
        idToEpicMap.clear();
        prioritizedTasksByStartTime.clear();
        return deletedTasks;
    }

    public void checkForTimeOverlap(Task task) throws TimeOverlapException {
        if (task instanceof Epic) {
            return;
        }
        List<Task> allTasks = getPrioritizedTasks();

        Optional<Task> overlappingTask = allTasks.stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .filter(existingTask -> existingTask.getStartTime() != null && task.getStartTime() != null)
                .filter(existingTask -> isTimeOverlap(existingTask, task))
                .findFirst();

        if (overlappingTask.isPresent()) {
            throw new TimeOverlapException("The time of the task overlaps with another task.");
        }
    }

    private LocalDateTime getMinStartTime(LocalDateTime currentMinStartTime, LocalDateTime newStartTime) {
        if (currentMinStartTime == DEFAULT_NULL_TASK_START_TIME
                || (newStartTime != DEFAULT_NULL_TASK_START_TIME && newStartTime.isBefore(currentMinStartTime))) {
            return newStartTime;
        }
        return currentMinStartTime;
    }

    private LocalDateTime getMaxEndTime(LocalDateTime currentMaxEndTime, LocalDateTime newEndTime) {
        if (currentMaxEndTime == DEFAULT_NULL_TASK_START_TIME
                || (newEndTime != DEFAULT_NULL_TASK_START_TIME && newEndTime.isAfter(currentMaxEndTime))) {
            return newEndTime;
        }
        return currentMaxEndTime;
    }

    private void addToPrioritizedTasks(Task task) {
        prioritizedTasksByStartTime.add(task);
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasksByStartTime.remove(task);
    }

    private void addTask(int id, Task task) throws DuplicateTaskIdException {
        if (idToTaskMap.containsKey(id)) {
            throw new DuplicateTaskIdException(String.format(DUPLICATE_TASK_ID, id));
        }
        idToTaskMap.put(id, task);
        addToPrioritizedTasks(task);
    }

    private void addSubtask(int id, Subtask subtask) throws DuplicateTaskIdException {
        if (isEpicExisted(subtask.getEpicId())) {
            if (idToSubTaskMap.containsKey(id)) {
                throw new DuplicateTaskIdException(String.format(DUPLICATE_SUBTASK_ID, id));
            }

            idToSubTaskMap.put(id, subtask);
            addToPrioritizedTasks(subtask);

            idToEpicMap.get(subtask.getEpicId()).getSubtaskIdList().add(id);
            int epicId = subtask.getEpicId();
            changeEpicStatusAndTimeProperties(epicId);
        } else {
            throw new MissingEpicException(String.format("Epic with ID %d does not exist", subtask.getEpicId()));
        }
    }

    private void addEpic(int id, Epic epic) throws DuplicateTaskIdException {
        if (idToEpicMap.containsKey(id)) {
            throw new DuplicateTaskIdException(String.format(DUPLICATE_EPIC_ID, id));
        }
        idToEpicMap.put(id, epic);
    }

    private void deleteSubtaskInsideEpic(Epic epic, int subtaskId) {
        epic.getSubtaskIdList().remove((Integer) subtaskId);
    }

    private void deleteAllSubTaskInsideAllEpic() {
        for (Epic epic : idToEpicMap.values()) {
            epic.getSubtaskIdList().clear();
            epic.setStatus(Status.NEW);
        }
    }

    private List<Task> deleteSubtaskConnectedWithEpic(Epic epic) {
        List<Task> deletedSubtasks = new ArrayList<>();
        for (int idSubTask : epic.getSubtaskIdList()) {
            deletedSubtasks.add(idToSubTaskMap.remove(idSubTask));
        }
        prioritizedTasksByStartTime.removeAll(deletedSubtasks);
        return deletedSubtasks;
    }

    private void changeEpicStatusAndTimeProperties(int epicId) {
        changeEpicStatus(epicId);
        calculateAndSetNewEpicTimeProperties(idToEpicMap.get(epicId));
    }

    private void changeEpicStatus(int epicId) {
        Status newEpicStatus = calculateNewEpicStatus(idToEpicMap.get(epicId));
        idToEpicMap.get(epicId).setStatus(newEpicStatus);
    }

    private void setNewTimeProperties(Epic epic, LocalDateTime startTime, int durationInMinutes, LocalDateTime endTime) {
        epic.setStartTime(startTime);
        epic.setDurationInMinutes(durationInMinutes);
        epic.setEndTime(endTime);
    }

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        return (startTime1.isBefore(endTime2) && startTime2.isBefore(endTime1));
    }

    private void removeTasksFromPrioritizedSetByType(Class<?> taskType) {
        prioritizedTasksByStartTime.removeIf(task -> task.getClass().equals(taskType));
    }
}
