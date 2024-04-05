package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node first;
    private Node last;
    private int actualHistorySize = 0;
    Map<Integer, Node> taskIdToTaskNode;


    public InMemoryHistoryManager() {
        this.taskIdToTaskNode = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        removeNode(taskIdToTaskNode.get(task.getId()));
        linkLast(task);

    }

    @Override
    public void remove(int id) {
        removeNode(taskIdToTaskNode.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node linkLast(Task task) {
        if (task == null) {
            return null;
        }

        final Node lastNode = last;
        final Node newNode = new Node(task, lastNode, null);
        last = newNode;

        if (first == null) {
            first = newNode;
        } else {
            lastNode.next = newNode;
        }
        actualHistorySize++;

        taskIdToTaskNode.put(task.getId(), newNode);
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> allTasksInHistory = new ArrayList<>();
        if (actualHistorySize == 0) {
            return allTasksInHistory;
        }

        Node current = first;

        while (current != null) {
            allTasksInHistory.add(current.task);
            current = current.next;
        }
        return allTasksInHistory;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (actualHistorySize == 0) {
            return;
        }

        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode == null) {
            first = nextNode;
            nextNode.prev = null;
        } else if (nextNode == null) {
            last = prevNode;
            prevNode.next = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }

        actualHistorySize--;
    }
}
