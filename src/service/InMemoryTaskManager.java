package service;

import exception.DuplicateTaskIdException;
import model.Epic;
import model.Task;
import repository.AllTasksRepository;

import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected AllTasksRepository allTasksRepository;
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected static int generatorTaskId = 0;

    public InMemoryTaskManager() {
        this.allTasksRepository = new AllTasksRepository();
        generatorTaskId = 0;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateTaskId());
        try {
            allTasksRepository.addTask(task);
        } catch (DuplicateTaskIdException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
        return task;
    }

    @Override
    public void updateTask(Task task) {
        allTasksRepository.updateTask(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return allTasksRepository.getAllTasks();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = allTasksRepository.getTaskById(id);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTaskType() {
        return allTasksRepository.getAllTaskType();
    }

    @Override
    public List<Task> getAllSubtaskType() {
        return allTasksRepository.getAllSubtaskType();
    }

    @Override
    public List<Task> getAllEpicType() {
        return allTasksRepository.getAllEpicType();
    }

    @Override
    public List<Task> getAllEpicSubtasks(Epic epic) {
        return allTasksRepository.getAllEpicSubtasks(epic);
    }

    @Override
    public List<Task> getAllEpicSubtasks(int id) {
        return allTasksRepository.getAllEpicSubtasks(id);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return allTasksRepository.getPrioritizedTasks();
    }

    @Override
    public List<Task> deleteTaskById(int id) {
        List<Task> deletedTasks = allTasksRepository.deleteTaskById(id);
        removeTasksFromHistoryManager(deletedTasks);
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllTaskType() {
        List<Task> deletedTasks = allTasksRepository.deleteAllTaskType();
        removeTasksFromHistoryManager(deletedTasks);
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllSubtaskType() {
        List<Task> deletedTasks = allTasksRepository.deleteAllSubtaskType();
        removeTasksFromHistoryManager(deletedTasks);
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllEpicType() {
        List<Task> deletedTasks = allTasksRepository.deleteAllEpicType();
        removeTasksFromHistoryManager(deletedTasks);
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllTasks() {
        List<Task> deletedTasks = allTasksRepository.deleteAllTasks();
        removeTasksFromHistoryManager(deletedTasks);
        return deletedTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public void printTaskList(List<Task> taskList) {
        for (Task task : taskList) {
            System.out.println(task);
        }
    }

    public void printPrioritizedTaskList(List<Task> taskList) {
        for (Task task : taskList) {
            System.out.println(task);
        }
    }

    private int generateTaskId() {
        generatorTaskId++;
        return generatorTaskId;
    }

    private void removeTasksFromHistoryManager(List<Task> tasksList) {
        for (Task task : tasksList) {
            historyManager.remove(task.getId());
        }
    }
}