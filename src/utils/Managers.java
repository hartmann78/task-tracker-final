package utils;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.FileBackedTasksManager;
import service.InMemoryTaskManager;

import java.io.IOException;

public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager loadFromFile(String path) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(path);
        String[] info = manager.read().split("\n");
        if (!info[0].isEmpty()) {
            boolean containsHistory = false;
            int limit = info.length;

            for (int i = 0; i < info.length; ++i) {
                if (info[i].isBlank()) {
                    limit = i;
                    containsHistory = true;
                }
            }

            for (int i = 0; i < limit; ++i) {
                String[] task = info[i].split(",");
                int id = Integer.parseInt(task[0]);
                String name = task[2];
                String description = task[4];
                Status status;
                if (task[3].equals("NEW")) {
                    status = Status.NEW;
                } else if (task[3].equals("IN_PROGRESS")) {
                    status = Status.IN_PROGRESS;
                } else {
                    status = Status.DONE;
                }

                if (task[1].equals("TASK")) {
                    manager.taskList.put(id, new Task(id, name, description, status));
                } else if (task[1].equals("EPIC")) {
                    manager.taskList.put(id, new Epic(id, name, description, status));
                } else {
                    int epicId = Integer.parseInt(task[5].substring(0, 1));
                    Epic epic = (Epic) manager.taskList.get(epicId);
                    epic.subtasks.put(id, new Subtask(id, name, description, status));
                }
            }

            if (containsHistory) {
                String[] history = info[info.length - 1].split(",");

                for (int i = history.length - 1; i >= 0; --i) {
                    manager.historyManager.addTaskToHistory(manager.getter(Integer.parseInt(history[i])));
                }
            }
        }

        System.out.println(manager.taskList.values());

        return manager;
    }
}
