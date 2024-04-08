import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import static constant.Constants.TASK_FILE_PATH;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();

        System.out.println("*********Tasks creation**************************************************************");
        Task task1 = taskManager.createTask(new Task("Task1", "Task1 description"));
        System.out.println("Task1 was created: " + task1);

        Task task2 = taskManager.createTask(new Task("Task2", "Task2 description"));
        System.out.println("Task2 was created: " + task2);

        Task epic1 = taskManager.createTask(new Epic("Epic1", "Epic1 description"));
        System.out.println("Epic2 was created: " + epic1);

        Task subtask1 = taskManager.createTask(new Subtask("Subtask1", "Subtask1 description", epic1.getId()));
        System.out.println("Subtask1 was created: " + subtask1);

        Task subtask11 = taskManager.createTask(new Subtask("Subtask11", "Subtask1 description", epic1.getId()));
        System.out.println("Subtask11 was created: " + subtask11);

        Task epic2 = taskManager.createTask(new Epic("Epic2", "Epic2 description"));
        System.out.println("Epic2 was created: " + epic2);

        Task subtask2 = taskManager.createTask(new Subtask("Subtask2", "Subtask2 description", epic2.getId()));
        System.out.println("Subtask2 was created: " + subtask2);

        System.out.println("*********History testing*************************************************************");
        taskManager.getTaskById(subtask2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(epic1.getId());
        System.out.println("All task in the history (subtask2, task1 and views, epic1): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("*********File Manager testing*************************************************************");
        FileBackedTasksManager newTasksManagerTest = new FileBackedTasksManager(TASK_FILE_PATH);
        System.out.println("Tasks in newTasksManagerTest: ");
        newTasksManagerTest.printTaskList(newTasksManagerTest.getAllTasks());
        System.out.println();
        System.out.println("History in newTasksManagerTest:");
        taskManager.printTaskList(newTasksManagerTest.getHistory());
    }
}

