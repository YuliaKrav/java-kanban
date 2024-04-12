package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private HttpClient client;
    private static final int SERVER_PORT = 8080;

    @BeforeEach
    public void setup() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testHandleGetRequest_GetTaskById_ExistingTask() throws Exception {
        Task testTask = new Task(1, "testTask1", "testTask1 description");
        taskManager.createTask(testTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/task/?id=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HTTP_OK, response.statusCode());

        Task responseTask = new Gson().fromJson(response.body(), Task.class);

        assertEquals(testTask.getId(), responseTask.getId());
    }

    @Test
    public void testHandleGetRequest_GetTaskById_NonExistingTask() throws IOException, InterruptedException, URISyntaxException {
        Task testTask = new Task(1, "testTask1", "testTask1 description");
        taskManager.createTask(testTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/task/?id=0"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HTTP_NOT_FOUND, response.statusCode());
        assertEquals("Task not found", response.body());
    }

    @Test
    public void testHandleGetRequest_GetAllTaskType() throws IOException, InterruptedException, URISyntaxException {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);
        Task epic = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/tasktype/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HTTP_OK, response.statusCode());

        Type taskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> allTaskType = new Gson().fromJson(response.body(), taskListType);
        assertEquals(2, allTaskType.size());
        assertTrue(allTaskType.contains(task1));
        assertTrue(allTaskType.contains(task2));
        assertFalse(allTaskType.contains(epic));
    }

    @Test
    public void testHandleGetRequest_GetEpicSubtasks() throws IOException, InterruptedException, URISyntaxException {
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        taskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 description", epic1.getId());
        taskManager.createTask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/subtask/epic/?id=" + epic1.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HTTP_OK, response.statusCode());

        Type subtaskListType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> returnedSubtasks = new Gson().fromJson(response.body(), subtaskListType);

        assertEquals(2, returnedSubtasks.size());

        List<String> returnedSubtaskNames = returnedSubtasks.stream().map(Subtask::getName).collect(Collectors.toList());
        assertTrue(returnedSubtaskNames.contains(subtask1.getName()));
        assertTrue(returnedSubtaskNames.contains(subtask2.getName()));
    }

    @Test
    public void testHandleGetRequest_GetAllSubtaskType() throws IOException, InterruptedException, URISyntaxException {
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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/subtask/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HTTP_OK, response.statusCode());

        Type subtaskListType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> allSubtaskType = new Gson().fromJson(response.body(), subtaskListType);
        assertEquals(3, allSubtaskType.size());
        assertTrue(allSubtaskType.contains(subtask1));
        assertTrue(allSubtaskType.contains(subtask2));
        assertTrue(allSubtaskType.contains(subtask3));
    }

    @Test
    public void testHandleGetRequest_GetAllEpicType() throws IOException, InterruptedException, URISyntaxException {
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        taskManager.createTask(subtask1);
        Epic epic2 = new Epic("Epic2", "Epic2 description");
        taskManager.createTask(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/epic/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HTTP_OK, response.statusCode());

        Type epicListType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> allEpicType = new Gson().fromJson(response.body(), epicListType);
        assertEquals(2, allEpicType.size());
        assertTrue(allEpicType.contains(epic1));
        assertTrue(allEpicType.contains(epic2));
    }

    @Test
    public void testHandlePostRequest() throws Exception {
        String jsonRequestBody = "{\"name\": \"Test task\", \"description\": \"Test description\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/tasktype/"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());

        Task responseTask = new Gson().fromJson(response.body(), Task.class);

        assertEquals("Test task", responseTask.getName());
        assertEquals("Test description", responseTask.getDescription());
    }

    @Test
    public void testHandlePutRequest_UpdateExistingTask() throws Exception {
        Task existingTask = new Task(1, "Old task", "Old description");
        taskManager.createTask(existingTask);

        String jsonRequestBody = "{\"id\": 1, \"name\": \"Updated task\", \"description\": \"Updated description\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/update/"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        assertEquals("Task successfully updated", response.body());

        Task updatedTask = taskManager.getTaskById(1);
        assertEquals("Updated task", updatedTask.getName());
        assertEquals("Updated description", updatedTask.getDescription());
    }

    @Test
    public void testHandlePutRequest_InvalidPath() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/invalidPath/"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.statusCode());
        assertEquals("Invalid path", response.body());
    }


    @Test
    public void testHandleDeleteRequest_DeleteTaskById() throws Exception {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/task/?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void testHandleDeleteRequest_DeleteAllTasks() throws Exception {
        Task task1 = new Task("Task1", "Task1 description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Task2 description");
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + SERVER_PORT + "/tasks/task/"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }
}