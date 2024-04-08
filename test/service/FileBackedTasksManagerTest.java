package service;

import constant.Constants;
import constant.Status;
import formatter.HistoryFormatter;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static constant.Constants.HISTORY_SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private static final String TEST_TASK_FILE_NAME = "test_file_with_tasks.csv";
    private static final Path TEST_TASK_FILE_PATH = Paths.get(TEST_TASK_FILE_NAME);

    @Override
    @BeforeEach
    public void setup() {
        try {
            Files.write(TEST_TASK_FILE_PATH, new byte[0]);
        } catch (IOException e) {
            fail("Failed to clear the test file: " + e.getMessage());
        }
        taskManager = new FileBackedTasksManager(TEST_TASK_FILE_PATH);
        assertNotNull(taskManager);
    }

    @Test
    public void testSaveHeader() {
        taskManager.deleteAllTasks();
        List<String> lines = readFromFile(TEST_TASK_FILE_PATH);
        assertTrue(lines.contains(Constants.CSV_HEADER), "CSV header should be present in the file.");
    }

    @Test
    public void testSaveTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task2);
        Task epic = new Epic("Epic1", "Epic1 description");
        taskManager.createTask(epic);
        Task subtask = new Subtask("Subtask1", "Subtask1 description", Status.NEW, epic.getId());
        taskManager.createTask(subtask);

        List<String> lines = readFromFile(TEST_TASK_FILE_PATH);

        assertTrue(lines.contains(task1.toCsvString()), "Task 1 should be saved correctly.");
        assertTrue(lines.contains(task2.toCsvString()), "Task 2 should be saved correctly.");
        assertTrue(lines.contains(subtask.toCsvString()), "Subtask should be saved correctly.");
        assertTrue(lines.contains(epic.toCsvString()), "Epic task should be saved correctly.");
    }

    @Test
    public void testSaveHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<String> allLines = readFromFile(TEST_TASK_FILE_PATH);

        int historySeparatorIndex = allLines.indexOf(HISTORY_SEPARATOR);
        assertNotEquals(-1, historySeparatorIndex, "History separator should be present in the file.");

        String expectedHistoryLine = HistoryFormatter.historyToString(taskManager.historyManager);
        String actualHistoryLine = allLines.get(historySeparatorIndex + 1);
        assertEquals(expectedHistoryLine, actualHistoryLine, "Expected and actual history lines should match.");
    }

    @Test
    public void testLoadTasks() {
        createTestFileWithTasks();
        taskManager = new FileBackedTasksManager(TEST_TASK_FILE_PATH);
        assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    public void testLoadHistory() {
        createTestFileWithHistory();
        taskManager = new FileBackedTasksManager(TEST_TASK_FILE_PATH);

        Integer[] expectedHistory = new Integer[]{1, 2};
        Integer[] actualHistory = taskManager.getHistory().stream().map(Task::getId).toArray(Integer[]::new);

        assertArrayEquals(expectedHistory, actualHistory, "Expected and actual history should match.");
    }

    @Test
    public void testLoadEpicWithSubtasks() {
        createTestFileWithEpicAndSubtasks();
        taskManager = new FileBackedTasksManager(TEST_TASK_FILE_PATH);

        Task epic = taskManager.getTaskById(3);
        assertTrue(epic instanceof Epic, "Task should be an instance of Epic.");

        List<Integer> expectedSubtasks = Arrays.asList(4, 5);
        List<Integer> actualSubtasks = ((Epic) epic).getSubtaskIdList();
        Collections.sort(expectedSubtasks);
        Collections.sort(actualSubtasks);

        assertEquals(expectedSubtasks, actualSubtasks, "Expected and actual subtask IDs should match.");
    }

    private void createTestFileWithTasks() {
        List<String> lines = new ArrayList<>();
        lines.add(Constants.CSV_HEADER);
        lines.add("1,TASK,Task1,NEW,Task1 description,");
        lines.add("2,TASK,Task2,NEW,Task2 description,");
        lines.add(HISTORY_SEPARATOR);
        lines.add("");
        writeToFile(lines, TEST_TASK_FILE_PATH);
    }

    private void createTestFileWithHistory() {
        List<String> lines = new ArrayList<>();
        lines.add(Constants.CSV_HEADER);
        lines.add("1,TASK,Task1,NEW,Task1 description,null,0,null,");
        lines.add("2,TASK,Task1,NEW,Task1 description,null,0,null,");
        lines.add(Constants.HISTORY_SEPARATOR);
        lines.add("1,2");
        writeToFile(lines, TEST_TASK_FILE_PATH);
    }

    private void createTestFileWithEpicAndSubtasks() {
        List<String> lines = new ArrayList<>();
        lines.add(Constants.CSV_HEADER);
        lines.add("1,TASK,Task1,NEW,Task1 description,");
        lines.add("3,EPIC,Epic1,NEW,Epic1 description,");
        lines.add("4,SUBTASK,Subtask1,NEW,Subtask1 description,3");
        lines.add("5,SUBTASK,Subtask11,NEW,Subtask1 description,3");
        lines.add(HISTORY_SEPARATOR);
        lines.add("");
        writeToFile(lines, TEST_TASK_FILE_PATH);
    }

    private List<String> readFromFile(Path filePath) {
        try {
            return Files.readAllLines(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read from file", ex);
        }
    }

    private void writeToFile(List<String> lines, Path filePath) {
        try {
            Files.write(filePath, lines);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write to file", ex);
        }
    }
}
