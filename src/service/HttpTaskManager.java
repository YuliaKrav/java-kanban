package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.KVClientException;
import exception.TaskLoadingException;
import exception.TaskSavingException;
import formatter.LocalDateTimeAdapter;
import kvclient.KVTaskClient;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskTypeAdapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private static final String TASK_KEY = "tasks";
    private final Gson gson;

    public HttpTaskManager(String serverUrl) throws IOException, URISyntaxException, InterruptedException, KVClientException {
        super(null);
        this.kvTaskClient = new KVTaskClient(serverUrl);

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                .registerTypeAdapter(Epic.class, new TaskTypeAdapter())
                .registerTypeAdapter(Subtask.class, new TaskTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    protected void save() {
        String jsonData = gson.toJson(getAllTasks());
        try {
            kvTaskClient.put(TASK_KEY, jsonData);
        } catch (KVClientException ex) {
            throw new TaskSavingException("Error saving tasks", ex);
        }
    }

    @Override
    public void load() {
        try {
            String jsonData = kvTaskClient.load(TASK_KEY);
            Task[] tasks = gson.fromJson(jsonData, Task[].class);

            Arrays.stream(tasks)
                    .filter(task -> task instanceof Epic)
                    .forEach(this::internalCreateTaskWithId);

            Arrays.stream(tasks)
                    .filter(task -> !(task instanceof Epic))
                    .forEach(this::internalCreateTaskWithId);

            synchronizeTaskIdGenerator(Arrays.asList(tasks));
        } catch (KVClientException ex) {
            throw new TaskLoadingException("Error loading tasks", ex);
        }
    }
}