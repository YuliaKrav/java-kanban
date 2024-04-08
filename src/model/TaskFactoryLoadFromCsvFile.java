package model;

import constant.Status;
import constant.TaskType;
import exception.ManageFileNotWellFormedException;
import exception.ManagerSaveException;

import static constant.Constants.CSV_DELIMITER;

public class TaskFactoryLoadFromCsvFile {

    private static final int MINIMUM_FIELDS_COUNT = 5;
    private static final int ID_INDEX = 0;
    private static final int TYPE_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int STATUS_INDEX = 3;
    private static final int DESCRIPTION_INDEX = 4;
    private static final int EPIC_ID_INDEX = 5;

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

            switch (type) {
                case TASK:
                    return new Task(id, name, description, status);
                case SUBTASK:
                    int epicId = Integer.parseInt(taskFields[EPIC_ID_INDEX]);
                    return new Subtask(id, name, description, status, epicId);
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
