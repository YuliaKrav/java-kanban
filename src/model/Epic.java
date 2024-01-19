package model;

import constant.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIdList;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtaskIdList = new ArrayList<>();
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    @Override
    public String toString() {
        return "Epic{" + super.toString() +
                " subtaskIdList=" + subtaskIdList +
                '}';
    }
}

