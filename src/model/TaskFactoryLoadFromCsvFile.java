package model;

import constant.Status;
import constant.TaskType;
import exception.ManageFileNotWellFormedException;
import exception.ManagerSaveException;

import java.time.LocalDateTime;

import static constant.Constants.CSV_DELIMITER;
import static constant.Constants.DEFAULT_NULL_TASK_START_TIME_STRING;
import static constant.Constants.DEFAULT_TASK_START_TIME;

public class TaskFactoryLoadFromCsvFile {

    private static final int MINIMUM_FIELDS_COUNT = 8;
    private static final int ID_INDEX = 0;
    private static final int TYPE_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int STATUS_INDEX = 3;
    private static final int DESCRIPTION_INDEX = 4;
    private static final int START_TIME_INDEX = 5;
    private static final int DURATION_INDEX = 6;
    private static final int END_TIME_INDEX = 7;
    private static final int EPIC_ID_INDEX = 8;

    public static Task createTask(String csvString) {
        String[] taskFields = csvString.split(CSV_DELIMITER);
        if (taskFields.length < MINIMUM_FIELDS_COUNT) {
            throw new ManagerSaveException("Error: Insufficient number of fields.", new IllegalArgumentException());
        }
        try {
            int id = Integer.parseInt(taskFields[ID_INDEX]);
            TaskType type = TaskType.valueOf(taskFields[TYPE_INDEX]);
            String name = taskFields[NAME_INDEX];
            Status status = Status.valueOf(taskFields[STATUS_INDEX].toUpperCase());
            String description = taskFields[DESCRIPTION_INDEX];
            LocalDateTime startTime = DEFAULT_NULL_TASK_START_TIME_STRING.equals(taskFields[START_TIME_INDEX])
                    ? DEFAULT_TASK_START_TIME
                    : LocalDateTime.parse(taskFields[START_TIME_INDEX]);

            int duration = Integer.parseInt(taskFields[DURATION_INDEX]);

            switch (type) {
                case TASK:
                    return new Task(id, name, description, status, startTime, duration);
                case SUBTASK:
                    int epicId = Integer.parseInt(taskFields[EPIC_ID_INDEX]);
                    return new Subtask(id, name, description, status, epicId, startTime, duration);
                case EPIC:
                    return new Epic(id, name, description, status);
                default:
                    throw new ManageFileNotWellFormedException("Error: Unknown task type.");
            }
        } catch (NumberFormatException ex) {
            throw new ManagerSaveException("Error: Invalid number format.", ex);
        } catch (IllegalArgumentException ex) {
            throw new ManagerSaveException("Error: Invalid task type or status.", ex);
        }
    }
}