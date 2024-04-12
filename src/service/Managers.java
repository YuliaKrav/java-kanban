package service;

import java.io.IOException;
import java.net.URISyntaxException;

import static constant.Constants.KV_SERVER_URL;

public class Managers {

    public static TaskManager getDefaultTaskManager() throws Exception {
        try {
            return new HttpTaskManager(KV_SERVER_URL);
        } catch (IOException | URISyntaxException | InterruptedException ex) {
            throw new Exception("Failed to create HttpTaskManager", ex);
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
