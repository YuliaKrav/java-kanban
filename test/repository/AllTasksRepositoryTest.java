package repository;

import constant.Status;
import exception.TimeOverlapException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static constant.Constants.DEFAULT_NULL_TASK_END_TIME;
import static constant.Constants.DEFAULT_TASK_DURATION_IN_MINUTES;
import static constant.Constants.DEFAULT_TASK_START_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class AllTasksRepositoryTest {

    private AllTasksRepository allTasksRepository;

    @BeforeEach
    void setUp() {
        allTasksRepository = new AllTasksRepository();
    }

    /**
     * *    a.  For calculating the priority of an Epic. No subtasks.
     */
    @Test
    public void testEmptySubtasks() {
        Epic epic1 = new Epic(1, "Test Epic1", "Test Epic1 description", Status.NEW);
        allTasksRepository.addTask(epic1);
        assertEquals(Status.NEW, allTasksRepository.calculateNewEpicStatus(epic1));
    }


    /**
     * b.   For calculating the priority of an Epic. All subtasks with NEW status.
     */
    @Test
    public void testAllNewSubtasks() {
        Epic epic1 = new Epic(1, "Test Epic1", "Test Epic1 description", Status.NEW);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Subtask 1 description", Status.NEW, 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Subtask 2 description", Status.NEW, 1);

        allTasksRepository.addTask(epic1);
        allTasksRepository.addTask(subtask1);
        allTasksRepository.addTask(subtask2);

        List<Integer> epic1RelatedSubtasks = epic1.getSubtaskIdList();

        assertEquals(2, epic1RelatedSubtasks.size());
        assertTrue(epic1RelatedSubtasks.contains(subtask1.getId()));
        assertTrue(epic1RelatedSubtasks.contains(subtask2.getId()));

        assertEquals(Status.NEW, allTasksRepository.calculateNewEpicStatus(epic1));
    }

    /**
     * c.    For calculating the priority of an Epic. All Subtasks with DONE status.
     */
    @Test
    public void testAllDoneSubtasks() {
        Epic epic1 = new Epic(1, "Test Epic1", "Test Epic1 description", Status.NEW);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Subtask 1 description", Status.DONE, 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Subtask 2 description", Status.DONE, 1);

        allTasksRepository.addTask(epic1);
        allTasksRepository.addTask(subtask1);
        allTasksRepository.addTask(subtask2);

        List<Integer> epic1RelatedSubtasks = epic1.getSubtaskIdList();

        assertEquals(2, epic1RelatedSubtasks.size());
        assertTrue(epic1RelatedSubtasks.contains(subtask1.getId()));
        assertTrue(epic1RelatedSubtasks.contains(subtask2.getId()));

        assertEquals(Status.DONE, allTasksRepository.calculateNewEpicStatus(epic1));
    }

    /**
     * d.   For calculating the priority of an Epic. All Subtasks with NEW and DONE status.
     */
    @Test
    public void testMixedNewAndDoneSubtasks() {
        Epic epic1 = new Epic(1, "Test Epic1", "Test Epic1 description", Status.NEW);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Subtask 1 description", Status.NEW, 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Subtask 2 description", Status.DONE, 1);

        allTasksRepository.addTask(epic1);
        allTasksRepository.addTask(subtask1);
        allTasksRepository.addTask(subtask2);

        List<Integer> epic1RelatedSubtasks = epic1.getSubtaskIdList();

        assertEquals(2, epic1RelatedSubtasks.size());
        assertTrue(epic1RelatedSubtasks.contains(subtask1.getId()));
        assertTrue(epic1RelatedSubtasks.contains(subtask2.getId()));

        assertEquals(Status.IN_PROGRESS, allTasksRepository.calculateNewEpicStatus(epic1));
    }

    /**
     * e.    For calculating the priority of an Epic. All Subtasks with IN_PROGRESS status.
     */
    @Test
    public void testInProgressSubtasks() {
        Epic epic1 = new Epic(1, "Test Epic1", "Test Epic1 description", Status.NEW);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Subtask 1 description", Status.IN_PROGRESS, 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Subtask 2 description", Status.IN_PROGRESS, 1);

        allTasksRepository.addTask(epic1);
        allTasksRepository.addTask(subtask1);
        allTasksRepository.addTask(subtask2);

        List<Integer> epic1RelatedSubtasks = epic1.getSubtaskIdList();

        assertEquals(2, epic1RelatedSubtasks.size());
        assertTrue(epic1RelatedSubtasks.contains(subtask1.getId()));
        assertTrue(epic1RelatedSubtasks.contains(subtask2.getId()));

        assertEquals(Status.IN_PROGRESS, allTasksRepository.calculateNewEpicStatus(epic1));
    }

    /**
     * For calculating an Epic start and end time. Epic without subtasks.
     */
    @Test
    public void testEpicTimePropertiesWithEmptySubtasks() {
        Epic epic = new Epic(1, "Test Epic", "Test Epic Description", Status.NEW);
        allTasksRepository.addTask(epic);
        allTasksRepository.calculateAndSetNewEpicTimeProperties(epic);

        assertEquals(DEFAULT_TASK_START_TIME, epic.getStartTime());
        assertEquals(DEFAULT_TASK_DURATION_IN_MINUTES, epic.getDurationInMinutes());
        assertEquals(DEFAULT_NULL_TASK_END_TIME, epic.getEndTime());
    }

    /**
     * For calculating an Epic start and end time. Epic with several subtasks.
     */

    @Test
    public void testEpicTimePropertiesWithMultipleSubtasks() {
        Epic epic = new Epic(1, "Test Epic", "Test Epic Description", Status.NEW);
        LocalDateTime subtask1StartDate = LocalDateTime.of(2023, 1, 1, 8, 0);
        int subtask1Duration = 60;
        LocalDateTime subtask2StartDate = LocalDateTime.of(2023, 1, 1, 12, 0);
        int subtask2Duration = 80;
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, 1, subtask1StartDate, subtask1Duration);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2", Status.DONE, 1, subtask2StartDate, subtask2Duration);

        allTasksRepository.addTask(epic);
        allTasksRepository.addTask(subtask1);
        allTasksRepository.addTask(subtask2);
        allTasksRepository.calculateAndSetNewEpicTimeProperties(epic);

        assertEquals(subtask1StartDate, epic.getStartTime());
        assertEquals(subtask1Duration + subtask2Duration, epic.getDurationInMinutes());
        assertEquals(subtask2StartDate.plusMinutes(subtask2Duration), epic.getEndTime());
    }

    /**
     * For calculating an Epic start and end time. Epic with updated subtask.
     */

    @Test
    public void testEpicTimePropertiesWithSubtaskUpdate() {
        Epic epic = new Epic(1, "Test Epic", "Test Epic Description", Status.NEW);

        LocalDateTime subtask1StartDate = LocalDateTime.of(2023, 1, 1, 8, 0);
        int subtask1Duration = 60;
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, 1, subtask1StartDate, subtask1Duration);

        allTasksRepository.addTask(epic);
        allTasksRepository.addTask(subtask1);
        allTasksRepository.calculateAndSetNewEpicTimeProperties(epic);
        assertEquals(subtask1StartDate, epic.getStartTime());
        assertEquals(subtask1Duration, epic.getDurationInMinutes());
        assertEquals(subtask1StartDate.plusMinutes(subtask1Duration), epic.getEndTime());

        LocalDateTime updatedSubtask1StartDate = LocalDateTime.of(2023, 1, 5, 12, 0);
        int updatedSubtask1Duration = 75;
        Subtask updatedSubtask1 = new Subtask(2, "Updated Subtask 1", "Updated Description 1", Status.NEW, 1, updatedSubtask1StartDate, updatedSubtask1Duration);
        allTasksRepository.updateTask(updatedSubtask1);
        allTasksRepository.calculateAndSetNewEpicTimeProperties(epic);

        assertEquals(updatedSubtask1StartDate, epic.getStartTime());
        assertEquals(updatedSubtask1Duration, epic.getDurationInMinutes());
        assertEquals(updatedSubtask1StartDate.plusMinutes(updatedSubtask1Duration), epic.getEndTime());
    }

    /**
     * No Overlapping. Tasks have different times.
     */
    @Test
    void testNoTimeOverlap() throws TimeOverlapException {
        LocalDateTime task1StartDate = LocalDateTime.of(2023, 9, 20, 9, 0);
        int task1Duration = 60;
        LocalDateTime task2StartDate = LocalDateTime.of(2023, 9, 20, 10, 0);
        int task2Duration = 45;

        Task task1 = new Task(1, "Task1", "Task1 description", Status.NEW, task1StartDate, task1Duration);
        Task task2 = new Task(2, "Task2", "Task2 description", Status.NEW, task2StartDate, task2Duration);

        allTasksRepository.addTask(task1);
        allTasksRepository.checkForTimeOverlap(task2);
    }

    /**
     * Overlapping. Tasks have overlapping times.
     */
    @Test
    void testTimeOverlap() {
        LocalDateTime task1StartDate = LocalDateTime.of(2023, 9, 20, 9, 0);
        int task1Duration = 60;
        LocalDateTime task2StartDate = LocalDateTime.of(2023, 9, 20, 9, 30);
        int task2Duration = 45;

        Task task1 = new Task(1, "Task1", "Task1 description", Status.NEW, task1StartDate, task1Duration);
        Task task2 = new Task(2, "Task2", "Task2 description", Status.NEW, task2StartDate, task2Duration);

        try {
            allTasksRepository.addTask(task1);
            allTasksRepository.checkForTimeOverlap(task2);
            fail("Expected TimeOverlapException to be thrown");
        } catch (TimeOverlapException e) {
            assertTrue(e.getMessage().contains("The time of the task overlaps with another task."));
        }
    }

    /**
     * Overlapping. Task with null start time.
     */
    @Test
    void testTimeOverlapWithNullStartTime() throws TimeOverlapException {
        LocalDateTime task1StartDate = null;
        int task1Duration = 60;
        LocalDateTime task2StartDate = LocalDateTime.of(2023, 9, 20, 9, 0);
        int task2Duration = 45;

        Task task1 = new Task(1, "Task1", "Task1 description", Status.NEW, task1StartDate, task1Duration);
        Task task2 = new Task(2, "Task2", "Task2 description", Status.NEW, task2StartDate, task2Duration);

        allTasksRepository.addTask(task1);
        allTasksRepository.checkForTimeOverlap(task2);
    }
}
