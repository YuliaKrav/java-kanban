package service;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setup() {
        taskManager = new InMemoryTaskManager();
        assertNotNull(taskManager);
    }
}