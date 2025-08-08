package service;

import model.Task;
import java.util.List;

public interface HistoryManager {
    void addTaskToHistory(Task var1);

    void removeTaskInHistory(int var1);

    List<Task> getHistory();
}
