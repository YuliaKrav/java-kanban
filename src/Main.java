import constant.Status;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();

        System.out.println("*********Tasks creation**************************************************************");
        Task task1 = taskManager.createTask(new Task("Task1", "Task1 description"));
        System.out.println("Task1 was created: " + task1);

        Task epic1 = taskManager.createTask(new Epic("Epic1", "Epic1 description"));
        System.out.println("Epic1 was created: " + epic1);
        Task subtask1 = taskManager.createTask(new Subtask("Subtask1", "Subtask1 description", epic1.getId()));
        System.out.println("Subtask1 was created: " + subtask1);
        System.out.println("Subtask1 was added to Epic1: " + epic1);
        Task subtask2 = taskManager.createTask(new Subtask("Subtask2", "Subtask2 description", Status.DONE, epic1.getId()));
        System.out.println("Subtask2 was created: " + subtask2);
        System.out.println("Subtask2 was added to Epic1: " + epic1);
        Task subtask3 = taskManager.createTask(new Subtask("Subtask3", "Subtask3 description", Status.IN_PROGRESS, epic1.getId()));
        System.out.println("Subtask3 was created: " + subtask2);
        System.out.println("Subtask3 was added to Epic1: " + epic1);

        Task epic2 = taskManager.createTask(new Epic("Epic2", "Epic2 description"));
        System.out.println("Epic2 was created: " + epic2);

        System.out.println("*********History testing*************************************************************");
        taskManager.getTaskById(subtask2.getId());
        System.out.println("All task in the history (subtask2 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(task1.getId());
        System.out.println("All task in the history (task1 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(subtask1.getId());
        System.out.println("All task in the history (subtask1 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(epic1.getId());
        System.out.println("All task in the history (epic1 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");

        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        System.out.println("Task1 was changed manually: " + task1);
        System.out.println("All task in the history (task1 changes): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(subtask1.getId());
        System.out.println("All task in the history (subtask1 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(epic2.getId());
        System.out.println("All task in the history (epic2 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(subtask1.getId());
        System.out.println("All task in the history (subtask1 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        subtask1.setStatus(Status.DONE);
        taskManager.updateTask(subtask1);
        System.out.println("Subtask1 was changed manually: " + subtask1);
        System.out.println("All task in the history (subtask1 changes): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(epic1.getId());
        System.out.println("All task in the history (epic1 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.getTaskById(subtask3.getId());
        System.out.println("All task in the history (subtask3 view): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.deleteTaskById(subtask3.getId());

        System.out.println("All task in the history (subtask3 deletion): ");
        taskManager.printTaskList(taskManager.getHistory());

        System.out.println("-------------------------------------------------------------------------------------");
        taskManager.deleteTaskById(epic1.getId());

        System.out.println("All task in the history (epic1 deletion): ");
        taskManager.printTaskList(taskManager.getHistory());
    }
}
