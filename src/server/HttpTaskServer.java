package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_METHOD_NOT_ALLOWED = 405;

    private static final Pattern allTasksPattern = Pattern.compile("^/tasks/task/$");
    private static final Pattern taskByIdPattern = Pattern.compile("^/tasks/task/\\?id=(\\d+)$");
    private static final Pattern epicSubtasksPattern = Pattern.compile("^/tasks/subtask/epic/\\?id=(\\d+)$");
    private static final Pattern historyPattern = Pattern.compile("^/tasks/history/$");
    private static final Pattern prioritizedTasksPattern = Pattern.compile("^/tasks/$");
    private static final Pattern subtaskPattern = Pattern.compile("^/tasks/subtask/$");
    private static final Pattern epicPattern = Pattern.compile("^/tasks/epic/$");
    private static final Pattern taskTypePattern = Pattern.compile("^/tasks/tasktype/$");
    private static final Pattern updatePattern = Pattern.compile("^/tasks/update/$");


    private final TaskManager taskManager;
    private HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGetRequest(path, httpExchange);
                    break;
                case "POST":
                    handlePostRequest(path, httpExchange);
                    break;
                case "PUT":
                    handlePutRequest(path, httpExchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(path, httpExchange);
                    break;
                default:
                    handleResponse(httpExchange, "Method " + method + " is not allowed", HTTP_METHOD_NOT_ALLOWED);
                    break;
            }
        }
    }

    private void handleGetRequest(String path, HttpExchange httpExchange) throws IOException {
        String queryParameters = httpExchange.getRequestURI().getQuery();

        Matcher allTasksMatcher = allTasksPattern.matcher(path);
        Matcher taskByIdMatcher = taskByIdPattern.matcher(path + "?" + queryParameters);
        Matcher epicSubtasksMatcher = epicSubtasksPattern.matcher(path + "?" + queryParameters);
        Matcher historyMatcher = historyPattern.matcher(path);
        Matcher prioritizedTasksMatcher = prioritizedTasksPattern.matcher(path);
        Matcher subtaskMatcher = subtaskPattern.matcher(path);
        Matcher epicMatcher = epicPattern.matcher(path);
        Matcher taskTypeMatcher = taskTypePattern.matcher(path);

        if (taskByIdMatcher.find()) {
            // GET tasks/task/?id
            int taskId = Integer.parseInt(taskByIdMatcher.group(1));
            Task singleTask = taskManager.getTaskById(taskId);
            if (singleTask != null) {
                handleResponse(httpExchange, singleTask, HTTP_OK);
            } else {
                handleResponse(httpExchange, "Task not found", HTTP_NOT_FOUND);
            }
        } else if (epicSubtasksMatcher.find()) {
            // GET /tasks/subtask/epic/?id=
            int epicId = Integer.parseInt(epicSubtasksMatcher.group(1));
            List<Task> epicSubTasks = taskManager.getAllEpicSubtasks(epicId);
            handleResponse(httpExchange, epicSubTasks, HTTP_OK);
        } else if (taskTypeMatcher.find()) {
            // GET /tasks/tasktype/
            List<Task> allTaskType = taskManager.getAllTaskType();
            handleResponse(httpExchange, allTaskType, HTTP_OK);
        } else if (subtaskMatcher.find()) {
            // GET /tasks/subtask/
            List<Task> subtasks = taskManager.getAllSubtaskType();
            handleResponse(httpExchange, subtasks, HTTP_OK);
        } else if (epicMatcher.find()) {
            // GET /tasks/epic/
            List<Task> epics = taskManager.getAllEpicType();
            handleResponse(httpExchange, epics, HTTP_OK);
        } else if (historyMatcher.find()) {
            // GET /tasks/history/
            List<Task> history = taskManager.getHistory();
            handleResponse(httpExchange, history, HTTP_OK);
        } else if (allTasksMatcher.find()) {
            // GET tasks/task/
            List<Task> allTasks = taskManager.getAllTasks();
            handleResponse(httpExchange, allTasks, HTTP_OK);
        } else if (prioritizedTasksMatcher.find()) {
            // GET /tasks/
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            handleResponse(httpExchange, prioritizedTasks, HTTP_OK);
        } else {
            handleResponse(httpExchange, "Path not found", HTTP_NOT_FOUND);
        }
    }

    private void handleResponse(HttpExchange httpExchange, Object data, int statusCode) throws IOException {
        String response = data instanceof String ? (String) data : new Gson().toJson(data);
        httpExchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream responseBody = httpExchange.getResponseBody()) {
            responseBody.write(response.getBytes());
        }
    }

    private void handlePostRequest(String path, HttpExchange httpExchange) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes());
        Gson gson = new Gson();

        Matcher taskMatcher = taskTypePattern.matcher(path);
        Matcher epicMatcher = epicPattern.matcher(path);
        Matcher subtaskMatcher = subtaskPattern.matcher(path);

        if (taskMatcher.find()) {
            // POST /tasks/tasktype/
            Task task = gson.fromJson(requestBody, Task.class);
            Task newTask = taskManager.createTask(task);
            handleResponse(httpExchange, newTask, HTTP_CREATED);
        } else if (epicMatcher.find()) {
            // POST /tasks/epic/
            Epic epic = gson.fromJson(requestBody, Epic.class);
            Task newEpic = taskManager.createTask(epic);
            handleResponse(httpExchange, newEpic, HTTP_CREATED);
        } else if (subtaskMatcher.find()) {
            // POST /tasks/subtask/
            Subtask subtask = gson.fromJson(requestBody, Subtask.class);
            Task newSubtask = taskManager.createTask(subtask);
            handleResponse(httpExchange, newSubtask, HTTP_CREATED);
        } else {
            handleResponse(httpExchange, "Invalid path", HTTP_BAD_REQUEST);
        }
    }

    private void handlePutRequest(String path, HttpExchange httpExchange) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes());
        Gson gson = new Gson();

        Matcher updateMatcher = updatePattern.matcher(path);

        if (updateMatcher.find()) {
            // PUT /tasks/update/
            Task task = gson.fromJson(requestBody, Task.class);
            try {
                taskManager.updateTask(task);
                handleResponse(httpExchange, "Task successfully updated", HTTP_OK);
            } catch (Exception e) {
                handleResponse(httpExchange, "Task update failed: " + e.getMessage(), HTTP_BAD_REQUEST);
            }
        } else {
            handleResponse(httpExchange, "Invalid path", HTTP_BAD_REQUEST);
        }
    }

    private void handleDeleteRequest(String path, HttpExchange httpExchange) throws IOException {
        String queryParameters = httpExchange.getRequestURI().getQuery();

        Matcher taskByIdMatcher = taskByIdPattern.matcher(path + "?" + queryParameters);
        Matcher allTasksMatcher = allTasksPattern.matcher(path);
        Matcher allSubtasksMatcher = subtaskPattern.matcher(path);
        Matcher allEpicMatcher = epicPattern.matcher(path);
        Matcher allTaskTypeMatcher = taskTypePattern.matcher(path);


        if (taskByIdMatcher.find()) {
            // DELETE tasks/task/?id
            int taskId = Integer.parseInt(taskByIdMatcher.group(1));
            List<Task> remainingTasks = taskManager.deleteTaskById(taskId);
            if (remainingTasks != null) {
                handleResponse(httpExchange, remainingTasks, HTTP_OK);
            } else {
                handleResponse(httpExchange, "Error deleting task", HTTP_INTERNAL_ERROR);
            }
        } else if (allTasksMatcher.find()) {
            // DELETE tasks/task/
            taskManager.deleteAllTasks();
            handleResponse(httpExchange, "All tasks deleted successfully", HTTP_OK);
        } else if (allSubtasksMatcher.find()) {
            // DELETE tasks/subtask/
            List<Task> remainingSubtasks = taskManager.deleteAllSubtaskType();
            handleResponse(httpExchange, remainingSubtasks, HTTP_OK);
        } else if (allEpicMatcher.find()) {
            // DELETE tasks/epic/
            List<Task> remainingEpics = taskManager.deleteAllEpicType();
            handleResponse(httpExchange, remainingEpics, HTTP_OK);
        } else if (allTaskTypeMatcher.find()) {
            // DELETE tasks/tasktype/
            List<Task> remainingTaskTypes = taskManager.deleteAllTaskType();
            handleResponse(httpExchange, remainingTaskTypes, HTTP_OK);
        } else {
            handleResponse(httpExchange, "Path not found", HTTP_NOT_FOUND);
        }
    }

    public void stop() {
        System.out.println("Stopping the server on port " + PORT);
        server.stop(0);
    }

    public static void main(String[] args) throws Exception {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }
}
