package formatter;

import model.Task;
import service.HistoryManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static constant.Constants.CSV_DELIMITER;

public class HistoryFormatter {

    public static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        List<String> historyTasksIds = history.stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.toList());
        return String.join(CSV_DELIMITER, historyTasksIds);
    }

    public static List<Integer> historyFromString(String value) {
        String[] historyTasksIds = value.split(CSV_DELIMITER);
        return Arrays.stream(historyTasksIds)
                .filter(task -> !task.isEmpty())
                .map(Integer::parseInt).collect(Collectors.toList());
    }
}
