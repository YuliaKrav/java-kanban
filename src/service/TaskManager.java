package service;

import constant.TaskType;
import model.Epic;
import model.Task;
import repository.AllTasksRepository;

import java.util.List;

public class TaskManager {

    AllTasksRepository allTasksRepository;
    private static int generatorTaskId = 0;

    public TaskManager() {
        this.allTasksRepository = new AllTasksRepository();
    }

    public Task createTask(Task task) {
        task.setId(generateTaskId());
        allTasksRepository.addTask(task);
        return task;
    }

    public void updateTask(Task task) {
        allTasksRepository.updateTask(task);
    }

    public List<Task> getAllTasks() {
        return allTasksRepository.getAllTasks();
    }

    public Task getTaskById(int id) {
        return allTasksRepository.getTaskById(id);
    }

    public List<Task> getTasksByType(TaskType type) {
        return allTasksRepository.getTasksByType(type);
    }

    public List<Task> getAllEpicTasks(Epic epic) {
        return allTasksRepository.getAllEpicSubtasks(epic);
    }

    public Task deleteTaskById(int id) {
        return allTasksRepository.deleteTaskById(id);
    }

    public List<Task> deleteTasksByType(TaskType type) {
        return allTasksRepository.deleteTasksByType(type);
    }

    public void deleteAllTasks() {
        allTasksRepository.deleteAllTasks();
    }

    public void printAllTasks() {
        for (Task task : getAllTasks()) {
            System.out.println(task);
        }
    }

    private int generateTaskId() {
        generatorTaskId++;
        return generatorTaskId;
    }
}
