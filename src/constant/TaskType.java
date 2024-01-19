package constant;

public enum TaskType {
    TASK("Task"),
    SUBTASK("Subtask"),
    EPIC("Epic");

    private String taskType;

    TaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
