package constant;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Constants {

    public static final String CSV_HEADER = "id,type,name,status,description,starttime,duration,endtime,epic";
    public static final int TASK_DATA_START_INDEX_AFTER_HEADER = 1;
    public static final String CSV_DELIMITER = ",";
    public static final String HISTORY_SEPARATOR = "";
    public static final int UNASSIGNED_TASK_ID = 0;
    public static final String TASK_FILE_NAME = "file_with_tasks.csv";
    public static final Path TASK_FILE_PATH = Paths.get(TASK_FILE_NAME);
    public static final String DUPLICATE_TASK_ID = "Task with id %d already exists.";
    public static final String DUPLICATE_SUBTASK_ID = "Subtask with id %d already exists.";
    public static final String DUPLICATE_EPIC_ID = "Epic with id %d already exists.";
    public static final int DEFAULT_TASK_DURATION_IN_MINUTES = 0;
    public static final LocalDateTime DEFAULT_TASK_START_TIME = null;
    public static final LocalDateTime DEFAULT_NULL_TASK_START_TIME = null;
    public static final String DEFAULT_NULL_TASK_START_TIME_STRING = "null";
    public static final LocalDateTime DEFAULT_NULL_TASK_END_TIME = null;

}
