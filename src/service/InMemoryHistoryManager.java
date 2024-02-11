package service;

import constant.Constants;
import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    LinkedList<Task> taskHistory;

    public InMemoryHistoryManager() {
        this.taskHistory = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (taskHistory.size() >= Constants.MAX_TASK_HISTORY_SIZE) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
