package model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class TaskTypeAdapter extends TypeAdapter<Task> {

    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, Task task) {
        System.out.println("Processing class: " + task.getClass().getName());
        TaskTypeAnnotation taskType = task.getClass().getAnnotation(TaskTypeAnnotation.class);
        if (taskType == null) {
            throw new JsonParseException("No TaskType annotation on class " + task.getClass().getName());
        }

        JsonElement jsonElement = gson.toJsonTree(task);
        jsonElement.getAsJsonObject().addProperty("taskType", taskType.value());
        gson.toJson(jsonElement, out);
    }

    @Override
    public Task read(JsonReader in) throws IOException {
        JsonElement jsonElement = gson.fromJson(in, JsonElement.class);
        JsonElement taskTypeElement = jsonElement.getAsJsonObject().get("taskType");

        if (taskTypeElement == null) {
            throw new JsonParseException("No taskType found in json: " + jsonElement);
        }

        String taskType = taskTypeElement.getAsString();

        if ("TASK".equals(taskType)) {
            return gson.fromJson(jsonElement, Task.class);
        } else if ("EPIC".equals(taskType)) {
            return gson.fromJson(jsonElement, Epic.class);
        } else if ("SUBTASK".equals(taskType)) {
            return gson.fromJson(jsonElement, Subtask.class);
        }
        throw new JsonParseException("Unexpected taskType: " + taskType);
    }
}
