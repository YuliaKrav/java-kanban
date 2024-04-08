package model;

import constant.Constants;
import constant.Status;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        this(name, description, Status.NEW, epicId);
    }

    public Subtask(String name, String description, Status status, int epicId) {
        this(Constants.UNASSIGNED_TASK_ID, name, description, status, epicId);
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
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
