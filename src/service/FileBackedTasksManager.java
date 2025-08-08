package service;

import model.Epic;
import model.Subtask;
import model.Task;
import utils.ManagerSaveException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    public String filePath;
    InMemoryHistoryManager memoryHistoryManager = new InMemoryHistoryManager();

    public FileBackedTasksManager(String filePath) {
        this.filePath = filePath;
    }

    public String read() throws IOException {
        return Files.readString(Path.of(this.filePath));
    }

    public void save() throws ManagerSaveException {
        try {
            try (Writer writer = new FileWriter(this.filePath)) {
                for (Task task : this.taskList.values()) {
                    if (task instanceof Epic epic) {
                        String var13 = String.valueOf(epic.id);
                        writer.write(var13 + "," + Types.EPIC + "," + epic + "\n");

                        for (Subtask subtask : epic.subtasks.values()) {
                            var13 = String.valueOf(subtask.id);
                            writer.write(var13 + "," + Types.SUBTASK + "," + subtask + "," + epic.id + "\n");
                        }
                    } else {
                        String var10001 = String.valueOf(task.id);
                        writer.write(var10001 + "," + Types.TASK + "," + task + "\n");
                    }
                }

                if (this.memoryHistoryManager.getTasks() != null) {
                    StringBuilder text = new StringBuilder();

                    for (Task task : this.memoryHistoryManager.getTasks()) {
                        text.append(task.id).append(',');
                    }

                    String var15 = text.substring(0, text.length() - 1);
                    writer.write("\n" + var15);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public void epicStatusCheck() {
        super.epicStatusCheck();
        this.save();
    }

    public void deleteAll() {
        super.deleteAll();
        this.save();
    }

    public void getById() {
        super.getById();
        this.save();
    }

    public void create() {
        super.create();
        this.save();
    }

    public void updateTask() {
        super.updateTask();
        this.save();
    }

    public void updateStatus() {
        super.updateStatus();
        this.save();
    }

    public void deleteTask() {
        super.deleteTask();
        this.save();
    }

    enum Types {
        TASK,
        EPIC,
        SUBTASK
    }
}
