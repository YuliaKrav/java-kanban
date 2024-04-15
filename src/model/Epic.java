package model;

import constant.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@TaskTypeAnnotation("EPIC")
public class Epic extends Task {
    private List<Integer> subtaskIdList = new ArrayList<>();
    private LocalDateTime endTime = null;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" + super.toString() +
                " subtaskIdList=" + subtaskIdList +
                '}';
    }
}
