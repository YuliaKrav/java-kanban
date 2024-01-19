package model;

import constant.Status;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        this(name, description, Status.NEW, epicId);
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
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
}
