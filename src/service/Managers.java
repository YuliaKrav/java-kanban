package service;

import static constant.Constants.TASK_FILE_PATH;

public class Managers {

    public static TaskManager getDefaultTaskManager() {
        return FileBackedTasksManager.loadFromFile(TASK_FILE_PATH.toFile());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
