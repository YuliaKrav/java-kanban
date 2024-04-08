package model;

import constant.Constants;
import constant.Status;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private Status status;
    private String description;

    public Task(String name, String description) {
        this(name, description, Status.NEW);
    }

    public Task(String name, String description, Status status) {
        this(Constants.UNASSIGNED_TASK_ID, name, description, status);
    }

    public Task(int id, String name, String description) {
        this(id, name, description, Status.NEW);
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskTypeUpperCase() {
        return getClass().getSimpleName().toUpperCase();
    }

    public String toCsvString() {
        StringBuilder csvString = new StringBuilder();
        csvString.append(getId()).append(Constants.CSV_DELIMITER);
        csvString.append(getTaskTypeUpperCase()).append(Constants.CSV_DELIMITER);
        csvString.append(getName()).append(Constants.CSV_DELIMITER);
        csvString.append(getStatus()).append(Constants.CSV_DELIMITER);
        csvString.append(getDescription()).append(Constants.CSV_DELIMITER);
        return csvString.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && status == task.status && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }


}