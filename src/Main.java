import model.Epic;
import model.Subtask;
import model.Task;
import server.KVServer;
import service.HttpTaskManager;
import service.Managers;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager taskManager = Managers.getDefaultTaskManager();

        System.out.println("*********Prioritized tasks***********************************************************");
        List<Task> result = taskManager.getPrioritizedTasks();
        taskManager.printTaskList(result);

        System.out.println("*********Tasks creation**************************************************************");
        Task task1 = taskManager.createTask(new Task("Task1", "Task1 description"));
        System.out.println("Task1 was created: " + task1);

        Task task2 = taskManager.createTask(new Task("Task2", "Task2 description"));
        System.out.println("Task2 was created: " + task2);

        Task epic1 = taskManager.createTask(new Epic("Epic1", "Epic1 description"));
        System.out.println("Epic1 was created: " + epic1);

        LocalDateTime subtask1StartDate = LocalDateTime.of(2023, 1, 1, 8, 0);
        int subtask1Duration = 60;
        Task subtask1 = taskManager.createTask(new Subtask("Subtask1", "Subtask1 description", epic1.getId(), subtask1StartDate, subtask1Duration));
        System.out.println("Subtask1 was created: " + subtask1);

        LocalDateTime subtask11StartDate = LocalDateTime.of(2023, 1, 1, 12, 0);
        int subtask11Duration = 80;
        Task subtask11 = taskManager.createTask(new Subtask("Subtask11", "Subtask1 description", epic1.getId(), subtask11StartDate, subtask11Duration));
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

        System.out.println("*********Loading testing*************************************************************");
        TaskManager taskManager2 = Managers.getDefaultTaskManager();
        System.out.println("All task in the new Http Task Manager: ");
        HttpTaskManager httpTaskManager = (HttpTaskManager) taskManager2;
        httpTaskManager.load();
        httpTaskManager.printTaskList(httpTaskManager.getAllTasks());

        kvServer.stop();
    }
}
