import constant.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();

        Task task1 = taskManager.createTask(new Task("Task1", "Task1 description"));
        System.out.println("Task1 was created: " + task1);

        Task task2 = taskManager.createTask(new Task("Task2", "Task2 description", Status.DONE));
        System.out.println("Task2 was created: " + task2);

        Task task3 = taskManager.createTask(new Task("Task3", "Task3 description", Status.DONE));
        System.out.println("Task3 was created: " + task3);

        System.out.println();

        Task epic1 = taskManager.createTask(new Epic("Epic1", "Epic1 description"));
        System.out.println("Epic1 was created: " + epic1);

        Task epic2 = taskManager.createTask(new Epic("Epic2", "Epic2 description"));
        System.out.println("Epic2 was created: " + epic2);

        System.out.println();

        Task subtask1 = taskManager.createTask(new Subtask("Subtask1", "Subtask1 description", epic1.getId()));
        System.out.println("Subtask1 was created: " + subtask1);
        System.out.println("Subtask1 was added to Epic1: " + epic1);

        Task subtask2 = taskManager.createTask(new Subtask("Subtask2", "Subtask2 description", Status.DONE, epic1.getId()));
        System.out.println("Subtask2 was created: " + subtask2);
        System.out.println("Subtask2 was added to Epic1: " + epic1);

        System.out.println();

        Task subtask3 = taskManager.createTask(new Subtask("Subtask3", "Subtask3 description", epic2.getId()));
        System.out.println("Subtask3 was created: " + subtask3);
        System.out.println("Subtask3 was added to Epic2: " + epic2);

        System.out.println();

        System.out.println("All epic in the database: ");
        System.out.println(taskManager.getAllEpicType());

        System.out.println();

        taskManager.getTaskById(epic2.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println("All task in the history: ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println();

        taskManager.getTaskById(subtask2.getId());
        taskManager.getTaskById(subtask1.getId());
        System.out.println("All task in the database: ");
        System.out.println(taskManager.getAllTaskType());

        System.out.println();

        System.out.println("All task in the history: ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println();

        task1.setStatus(Status.DONE);
        System.out.println("Task1 was changed manually: " + task1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateTask(subtask1);
        System.out.println("Subtask1 was changed manually: " + subtask1);
        System.out.println("Epic1 status was changed automatically: " + epic1);
        epic1.setDescription("New Epic1 description");
        taskManager.updateTask(epic1);
        System.out.println("Epic1 was changed manually: " + epic1);

        System.out.println();

        System.out.println("Task1 was deleted: " + taskManager.deleteTaskById(task1.getId()));
        System.out.println("Subtask1 was deleted: " + taskManager.deleteTaskById(subtask1.getId()));
        System.out.println("Epic2 was deleted: " + taskManager.deleteTaskById(epic2.getId()));

        System.out.println();
        System.out.println("All tasks in the database: ");
        taskManager.printTaskList(taskManager.getAllTasks());
    }
}
