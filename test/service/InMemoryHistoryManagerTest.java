package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty after initialization.");
    }

    @Test
    public void testNoDuplicateTasksInHistory() {
        Task task1 = new Task(1, "Task1", "Task1 description");
        Task task2 = new Task(2, "Task2", "Task2 description");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "There should be two unique tasks in history.");
        assertEquals(task2, history.get(0), "The first task should be task2.");
        assertEquals(task1, history.get(1), "The second task should be task1.");
    }

    @Test
    public void testRemoveFirstTaskFromHistory() {
        Task task1 = new Task(1, "Task1", "Task1 description");
        Task task2 = new Task(2, "Task2", "Task2 description");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "There should be only one task left in history.");
        assertEquals(task2, history.get(0), "The remaining task should be task2.");
    }

    @Test
    public void testRemoveMiddleTaskFromHistory() {
        Task task1 = new Task(1, "Task1", "Task1 description");
        Task task2 = new Task(2, "Task2", "Task2 description");
        Task task3 = new Task(3, "Task3", "Task3 description");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "There should be two tasks left in history.");
        assertEquals(task1, history.get(0), "The first remaining task should be task1.");
        assertEquals(task3, history.get(1), "The second remaining task should be task3.");
    }

    @Test
    public void testRemoveLastTaskFromHistory() {
        Task task1 = new Task(1, "Task1", "Task1 description");
        Task task2 = new Task(2, "Task2", "Task2 description");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "There should be only one task left in history.");
        assertEquals(task1, history.get(0), "The remaining task should be task1.");
    }
}