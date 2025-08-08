package service;

import model.Epic;
import model.Subtask;
import model.Task;

public interface TaskManager {
    void epicStatusCheck();

    void showFunctions();

    void displayTasks();

    void deleteAll();

    void getById();

    void create();

    Task taskCreator(int var1, boolean var2);

    Epic epicCreator(int var1, boolean var2, boolean var3);

    Subtask subTaskCreator(int var1, int var2, boolean var3);

    void updateTask();

    void updateStatus();

    void deleteTask();
}
