package service;

import exception.KVClientException;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;
import java.net.URISyntaxException;

import static constant.Constants.KV_SERVER_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;

    @BeforeEach
    public void setup() throws IOException, URISyntaxException, InterruptedException, KVClientException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(KV_SERVER_URL);
        assertNotNull(taskManager);
    }

    @AfterEach
    public void tearDown() {
        kvServer.stop();
    }

    @Test
    public void testSaveAndLoadTasks() throws IOException, InterruptedException, URISyntaxException, KVClientException {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);

        HttpTaskManager newTaskManager = new HttpTaskManager(KV_SERVER_URL);
        newTaskManager.load();

        assertEquals(2, newTaskManager.getAllTasks().size());
        assertNotNull(newTaskManager.getTaskById(task1.getId()));
        assertNotNull(newTaskManager.getTaskById(task2.getId()));
    }
}