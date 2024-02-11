package service;

import constant.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    public void testCreateTask_StandardBehavior() {
        Task task = new Task("Task1", "Task1 description");
        Task createdTask = taskManager.createTask(task);
        assertNotNull(createdTask);
        assertEquals(1, taskManager.getAllTasks().size(), "Only one task should be created.");
    }

    @Test
    public void testCreateTask_EmptyList() {
        assertEquals(0, taskManager.getAllTasks().size(), "Task list should be empty initially.");
    }

    @Test
    public void testCreateTask_InvalidId() {
        Task task = taskManager.getTaskById(-1);
        assertNull(task);
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("OriginalTask", "Original description");
        Task createdTask = taskManager.createTask(task);
        assertNotNull(createdTask);

        createdTask.setName("UpdatedTask");
        createdTask.setDescription("Updated description");
        taskManager.updateTask(createdTask);

        Task updatedTask = taskManager.getTaskById(createdTask.getId());
        assertEquals("UpdatedTask", updatedTask.getName(), "Task name should be updated.");
        assertEquals("Updated description", updatedTask.getDescription(), "Task description should be updated.");
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);
        Task epic = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic);
        Task subtask = new Subtask("Subtask1", "Subtask1 description", Status.NEW, epic.getId());
        taskManager.createTask(subtask);

        List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(4, allTasks.size(), "All four types of tasks should be present.");
        assertTrue(allTasks.contains(task1), "Task1 should be in the list.");
        assertTrue(allTasks.contains(task2), "Task2 should be in the list.");
        assertTrue(allTasks.contains(epic), "Epic1 should be in the list.");
        assertTrue(allTasks.contains(subtask), "Subtask1 should be in the list.");
    }

    @Test
    public void testGetTaskById_ExistingTask() {
        Task task = new Task("Task1", "Task1 description");
        Task createdTask = taskManager.createTask(task);
        Task receivedTask = taskManager.getTaskById(createdTask.getId());
        assertNotNull(receivedTask);
        assertEquals(task.getName(), receivedTask.getName());
        assertEquals(task.getDescription(), receivedTask.getDescription());
    }

    @Test
    public void testGetTaskById_NonExistingTask() {
        Task receivedTask = taskManager.getTaskById(-1);
        assertNull(receivedTask);
    }

    @Test
    public void testGetAllTaskType() {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);
        Task epic = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic);

        List<Task> allTaskType = taskManager.getAllTaskType();
        assertEquals(2, allTaskType.size());
        assertTrue(allTaskType.contains(task1));
        assertTrue(allTaskType.contains(task2));
        assertFalse(allTaskType.contains(epic));
    }

    @Test
    public void testGetAllSubtaskType() {
        Task epic = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        taskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        taskManager.createTask(subtask2);

        Epic epic3 = new Epic("Epic3", "Epic3 description");
        taskManager.createTask(epic3);
        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 description", epic3.getId());
        taskManager.createTask(subtask3);

        List<Task> allSubtaskType = taskManager.getAllSubtaskType();
        assertEquals(3, allSubtaskType.size());
        assertTrue(allSubtaskType.contains(subtask1));
        assertTrue(allSubtaskType.contains(subtask2));
        assertTrue(allSubtaskType.contains(subtask3));
    }

    @Test
    public void testGetAllEpicType() {
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        taskManager.createTask(subtask1);
        Epic epic2 = new Epic("Epic2", "Epic2 description");
        taskManager.createTask(epic2);

        List<Task> allEpicType = taskManager.getAllEpicType();
        assertEquals(2, allEpicType.size());
        assertTrue(allEpicType.contains(epic1));
        assertTrue(allEpicType.contains(epic2));
    }

    @Test
    public void testGetAllEpicSubtasks() {
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        taskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 description", epic1.getId());
        taskManager.createTask(subtask2);

        Epic epic3 = new Epic("Epic3", "Epic3 description");
        taskManager.createTask(epic3);
        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 description", epic3.getId());
        taskManager.createTask(subtask3);

        List<Task> allEpicSubtasks = taskManager.getAllEpicSubtasks(epic1);
        assertEquals(2, allEpicSubtasks.size());
        assertTrue(allEpicSubtasks.contains(subtask1));
        assertTrue(allEpicSubtasks.contains(subtask2));
    }

    @Test
    public void testDeleteTaskById() {
        Task task = new Task("Task1", "Task1 description");
        Task createdTask = taskManager.createTask(task);
        assertNotNull(createdTask);

        Task deletedTask = taskManager.deleteTaskById(createdTask.getId());
        assertEquals(createdTask, deletedTask, "Deleted task should be the same as the created task.");
        assertNull(taskManager.getTaskById(createdTask.getId()), "Task should be deleted, so it should return null.");
    }

    @Test
    public void testDeleteAllTaskType() {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);

        List<Task> deletedTasks = taskManager.deleteAllTaskType();

        assertEquals(2, deletedTasks.size());
        assertTrue(deletedTasks.contains(task1));
        assertTrue(deletedTasks.contains(task2));

        List<Task> remainingTasks = taskManager.getAllTaskType();
        assertEquals(0, remainingTasks.size());
    }

    @Test
    public void testDeleteAllSubtaskType() {
        Task epic = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic);
        Task subtask1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        taskManager.createTask(subtask1);
        Task subtask2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        taskManager.createTask(subtask2);

        List<Task> deletedTasks = taskManager.deleteAllSubtaskType();

        assertEquals(2, deletedTasks.size());
        assertTrue(deletedTasks.contains(subtask1));
        assertTrue(deletedTasks.contains(subtask2));

        List<Task> remainingTasks = taskManager.getAllSubtaskType();
        assertEquals(0, remainingTasks.size());
    }

    @Test
    public void testDeleteAllEpicType() {
        Task epic1 = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic1);
        Task epic2 = new Epic("Epic2", "Epic2 description");
        taskManager.createTask(epic2);

        List<Task> deletedTasks = taskManager.deleteAllEpicType();

        assertEquals(2, deletedTasks.size());
        assertTrue(deletedTasks.contains(epic1));
        assertTrue(deletedTasks.contains(epic2));

        List<Task> remainingTasks = taskManager.getAllEpicType();
        assertEquals(0, remainingTasks.size());
    }

    @Test
    public void testDeleteEpicAlsoDeletesSubtasks() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        taskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        taskManager.createTask(subtask2);

        assertNotNull(taskManager.getTaskById(subtask1.getId()));
        assertNotNull(taskManager.getTaskById(subtask2.getId()));

        taskManager.deleteAllEpicType();

        assertNull(taskManager.getTaskById(subtask1.getId()));
        assertNull(taskManager.getTaskById(subtask2.getId()));
    }

    @Test
    public void testDeleteAllTasks() {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);

        List<Task> deletedTasks = taskManager.deleteAllTasks();
        assertEquals(2, deletedTasks.size());
        assertTrue(deletedTasks.contains(task1));
        assertTrue(deletedTasks.contains(task2));

        assertEquals(0, taskManager.getAllTasks().size());
    }
}