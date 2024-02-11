package service;

import constant.Constants;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void testHistoryLimit() {
        for (int i = 1; i <= Constants.MAX_TASK_HISTORY_SIZE + 1; i++) {
            historyManager.add(new Task(i, "Task" + i, "Task" + i + " description"));
        }
        assertEquals(Constants.MAX_TASK_HISTORY_SIZE, historyManager.getHistory().size(), "History size should not exceed " + Constants.MAX_TASK_HISTORY_SIZE + ".");
        assertEquals(2, historyManager.getHistory().get(0).getId(), "The first task in the history should be the one with ID 2 after exceeding the limit by 1.");
    }

}