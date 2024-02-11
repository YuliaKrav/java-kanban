package repository;

import constant.Status;
import constant.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllTasksRepository {
    HashMap<Integer, Task> idToTaskMap;
    HashMap<Integer, Epic> idToEpicMap;
    HashMap<Integer, Subtask> idToSubTaskMap;

    public AllTasksRepository() {
        this.idToTaskMap = new HashMap<>();
        this.idToEpicMap = new HashMap<>();
        this.idToSubTaskMap = new HashMap<>();
    }

    public void addTask(Task task) {
        if (task.getClass().getSimpleName().equals(TaskType.TASK.getTaskType())) {
            addTask(task.getId(), task);
        } else if (task.getClass().getSimpleName().equals(TaskType.SUBTASK.getTaskType())) {
            addSubtask(task.getId(), (Subtask) task);
        } else if (task.getClass().getSimpleName().equals(TaskType.EPIC.getTaskType())) {
            addEpic(task.getId(), (Epic) task);
        }
    }

    public void updateTask(Task task) {
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
            idToTaskMap.put(id, task);
        }
    }

    private void updateSubtask(int id, Subtask subtask) {
        if (isSubtaskExisted(subtask) && idToSubTaskMap.get(id).getEpicId().equals(subtask.getEpicId())) {
            idToSubTaskMap.put(id, subtask);
            int epicId = subtask.getEpicId();
            changeEpicStatus(epicId);
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

    public Task deleteTaskById(int id) {
        if (idToTaskMap.containsKey(id)) {
            return idToTaskMap.remove(id);
        }

        if (idToSubTaskMap.containsKey(id)) {
            int epicId = idToSubTaskMap.get(id).getEpicId();
            Epic epic = idToEpicMap.get(epicId);
            deleteSubtaskInsideEpic(epic, id);
            Status newEpicStatus = calculateNewEpicStatus(epic);
            epic.setStatus(newEpicStatus);
            idToEpicMap.put(epicId, epic);
            return idToSubTaskMap.remove(id);
        }

        if (idToEpicMap.containsKey(id)) {
            deleteSubtaskConnectedWithEpic(idToEpicMap.get(id));
            return idToEpicMap.remove(id);
        }
        return null;
    }

    public List<Task> deleteAllTaskType() {
        List<Task> deletedTasks = new ArrayList<>(idToTaskMap.values());
        idToTaskMap.clear();
        return deletedTasks;
    }

    public List<Task> deleteAllSubtaskType() {
        List<Task> deletedTasks = new ArrayList<>(idToSubTaskMap.values());
        idToSubTaskMap.clear();
        deleteAllSubTaskInsideAllEpic();
        return deletedTasks;
    }

    public List<Task> deleteAllEpicType() {
        List<Task> deletedTasks = new ArrayList<>(idToEpicMap.values());
        idToSubTaskMap.clear();
        idToEpicMap.clear();
        return deletedTasks;
    }

    public List<Task> deleteAllTasks() {
        List<Task> deletedTasks = getAllTasks();

        idToTaskMap.clear();
        idToSubTaskMap.clear();
        idToEpicMap.clear();
        return deletedTasks;
    }

    private void addTask(int id, Task task) {
        idToTaskMap.put(id, task);
    }

    private void addSubtask(int id, Subtask subtask) {
        if (isEpicExisted(subtask.getEpicId())) {
            idToSubTaskMap.put(id, subtask);
            idToEpicMap.get(subtask.getEpicId()).getSubtaskIdList().add(id);
            int epicId = subtask.getEpicId();
            changeEpicStatus(epicId);
        }
    }

    private void addEpic(int id, Epic epic) {
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

    private void deleteSubtaskConnectedWithEpic(Epic epic) {
        for (int idSubTask : epic.getSubtaskIdList()) {
            idToSubTaskMap.remove(idSubTask);
        }
    }

    private void changeEpicStatus(int epicId) {
        Status newEpicStatus = calculateNewEpicStatus(idToEpicMap.get(epicId));
        idToEpicMap.get(epicId).setStatus(newEpicStatus);
    }
}
