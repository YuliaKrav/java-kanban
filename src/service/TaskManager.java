package service;

import model.Epic;
import model.Task;

import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    void updateTask(Task task);

    List<Task> getAllTasks();

    Task getTaskById(int id);

    List<Task> getAllTaskType();

    List<Task> getAllSubtaskType();

    List<Task> getAllEpicType();

    List<Task> getAllEpicSubtasks(Epic epic);

    Task deleteTaskById(int id);

    List<Task> deleteAllTaskType();

    List<Task> deleteAllSubtaskType();

    List<Task> deleteAllEpicType();

    List<Task> deleteAllTasks();

    List<Task> getHistory();

    void printTaskList(List<Task> taskList);
}

