package service;

import exception.DuplicateTaskIdException;
import exception.ManageFileNotWellFormedException;
import exception.ManagerFileNotFoundException;
import exception.ManagerLoadException;
import exception.ManagerSaveException;
import formatter.HistoryFormatter;
import model.Epic;
import model.Task;
import model.TaskFactoryLoadFromCsvFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static constant.Constants.CSV_HEADER;
import static constant.Constants.HISTORY_SEPARATOR;
import static constant.Constants.TASK_DATA_START_INDEX_AFTER_HEADER;
import static constant.Constants.TASK_FILE_PATH;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final Path filePath;
    private boolean isLoaded = false;

    public FileBackedTasksManager(Path filePath) throws IOException {
        if (filePath == null) {
            this.filePath = TASK_FILE_PATH;
        } else {
            this.filePath = filePath;
            if (!isLoaded) {
                load();
                isLoaded = true;
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file.toPath());
        if (!fileBackedTasksManager.isLoaded) {
            fileBackedTasksManager.load();
            fileBackedTasksManager.isLoaded = true;
        }
        return fileBackedTasksManager;
    }

    protected void save() {
        try {
            List<String> taskLines = getAllTasks()
                    .stream()
                    .map(Task::toCsvString)
                    .collect(Collectors.toList());
            String historyLine = HistoryFormatter.historyToString(historyManager);

            List<String> allLines = new ArrayList<>();
            allLines.add(CSV_HEADER);
            allLines.addAll(taskLines);
            allLines.add(HISTORY_SEPARATOR);
            allLines.add(historyLine);

            saveToFile(allLines, filePath);


        } catch (IOException ex) {
            throw new ManagerSaveException("An error occurred while saving data.", ex);
        }
    }

    public void load() {
        try {
            if (Files.exists(filePath)) {
                List<String> allLines = readFromFile(filePath);
                if (allLines.isEmpty()) {
                    System.out.println("File is empty, continuing without loading tasks.");
                    return;
                }

                int historySeparatorIndex = allLines.indexOf(HISTORY_SEPARATOR);
                if (historySeparatorIndex == -1 || historySeparatorIndex < TASK_DATA_START_INDEX_AFTER_HEADER) {
                    throw new ManageFileNotWellFormedException("File is not well-formed.");

                }

                List<Task> tasksFromFile = allLines.subList(TASK_DATA_START_INDEX_AFTER_HEADER, historySeparatorIndex)
                        .stream()
                        .map(TaskFactoryLoadFromCsvFile::createTask)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                tasksFromFile.stream()
                        .filter(task -> task instanceof Epic)
                        .forEach(this::internalCreateTaskWithId);

                tasksFromFile.stream()
                        .filter(task -> !(task instanceof Epic))
                        .forEach(this::internalCreateTaskWithId);

                synchronizeTaskIdGenerator(tasksFromFile);

                String historyFromFile = allLines.get(historySeparatorIndex + 1);
                List<Integer> historyTasksIds = HistoryFormatter.historyFromString(historyFromFile);
                historyTasksIds.forEach(taskId -> {
                    Task task = internalGetTaskById(taskId);

                    internalAddToHistory(task);
                });
            } else {
                throw new ManagerFileNotFoundException("The file does not exist.");
            }
        } catch (IOException ex) {
            throw new ManagerLoadException("An error occurred while loading data.", ex);
        }
    }

    void internalCreateTaskWithId(Task task) {
        try {
            allTasksRepository.addTask(task);
        } catch (DuplicateTaskIdException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private Task internalGetTaskById(int id) {
        return allTasksRepository.getTaskById(id);
    }

    private void internalAddToHistory(Task task) {
        historyManager.add(task);
    }

    private void saveToFile(List<String> lines, Path filePath) throws IOException {
        Files.write(filePath, lines);
    }

    private List<String> readFromFile(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }

    void synchronizeTaskIdGenerator(List<Task> tasksFromFile) {
        generatorTaskId = tasksFromFile.stream()
                .map(Task::getId)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Task getTaskById(int id) {
        Task receivedTask = super.getTaskById(id);
        save();
        return receivedTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public List<Task> deleteTaskById(int id) {
        List<Task> deletedTasks = super.deleteTaskById(id);
        save();
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllTaskType() {
        List<Task> deletedTasks = super.deleteAllTaskType();
        save();
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllSubtaskType() {
        List<Task> deletedTasks = super.deleteAllSubtaskType();
        save();
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllEpicType() {
        List<Task> deletedTasks = super.deleteAllEpicType();
        save();
        return deletedTasks;
    }

    @Override
    public List<Task> deleteAllTasks() {
        List<Task> deletedTasks = super.deleteAllTasks();
        save();
        return deletedTasks;
    }
}
