package service;

import model.Epic;
import model.Task;
import repository.AllTasksRepository;

import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    AllTasksRepository allTasksRepository;
    HistoryManager historyManager = Managers.getDefaultHistory();
    private static int generatorTaskId = 0;

    public InMemoryTaskManager() {
        this.allTasksRepository = new AllTasksRepository();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateTaskId());
        allTasksRepository.addTask(task);
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
    public Task deleteTaskById(int id) {
        return allTasksRepository.deleteTaskById(id);
    }

    @Override
    public List<Task> deleteAllTaskType() {
        return allTasksRepository.deleteAllTaskType();
    }

    @Override
    public List<Task> deleteAllSubtaskType() {
        return allTasksRepository.deleteAllSubtaskType();
    }

    @Override
    public List<Task> deleteAllEpicType() {
        return allTasksRepository.deleteAllEpicType();
    }

    @Override
    public List<Task> deleteAllTasks() {
        return allTasksRepository.deleteAllTasks();
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

    private int generateTaskId() {
        generatorTaskId++;
        return generatorTaskId;
    }
}

