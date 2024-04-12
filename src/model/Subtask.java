package model;

import constant.Constants;
import constant.Status;

import java.time.LocalDateTime;

import static constant.Constants.DEFAULT_TASK_DURATION_IN_MINUTES;
import static constant.Constants.DEFAULT_TASK_START_TIME;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        this(name, description, Status.NEW, epicId);
    }

    public Subtask(String name, String description, Status status, int epicId) {
        this(Constants.UNASSIGNED_TASK_ID, name, description, status, epicId, DEFAULT_TASK_START_TIME, DEFAULT_TASK_DURATION_IN_MINUTES);
    }

    public Subtask(String name, String description, int epicId, LocalDateTime startDate, int duration) {
        this(Constants.UNASSIGNED_TASK_ID, name, description, Status.NEW, epicId, startDate, duration);
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId, LocalDateTime startDate, int duration) {
        super(id, name, description, status, startDate, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" + super.toString() +
                " epicId=" + epicId +
                '}';
    }

    @Override
    public String toCsvString() {
        StringBuilder csvString = new StringBuilder(super.toCsvString());
        csvString.append(getEpicId());
        return csvString.toString();
    }
}
