package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

public class InMemoryTaskManager implements TaskManager {
    public int id = 0;
    Scanner scanner;
    public HashMap<Integer, Task> taskList;
    public InMemoryHistoryManager historyManager;

    public InMemoryTaskManager() {
        this.scanner = new Scanner(System.in);
        this.taskList = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
    }

    public void returnId() {
        for (Task task : this.taskList.values()) {
            this.id = task.id;
            if (task instanceof Epic epic) {
                for (Subtask subtask : epic.subtasks.values()) {
                    this.id = subtask.id;
                }
            }
        }
    }

    public void showFunctions() {
        System.out.println("Выберите функцию:");
        System.out.println("1 - получение списка задач");
        System.out.println("2 - удаление всех задач");
        System.out.println("3 - получение по идентификатору");
        System.out.println("4 - создание задач");
        System.out.println("5 - обновление данных");
        System.out.println("6 - обновление статуса");
        System.out.println("7 - удаление по идентификатору");
        System.out.println("8 - история просмотров задач");
        System.out.println("9 - завершить работу");
    }

    public void epicStatusCheck() {
        for (Task task : this.taskList.values()) {
            int newCount = 0;
            int doneCount = 0;
            if (task instanceof Epic updateEpic) {
                if (!updateEpic.subtasks.isEmpty()) {
                    for (Subtask subtask : updateEpic.subtasks.values()) {
                        if (subtask.status == Status.NEW) {
                            ++newCount;
                        } else if (subtask.status == Status.DONE) {
                            ++doneCount;
                        }
                    }

                    if (newCount == updateEpic.subtasks.size()) {
                        updateEpic.setStatus(Status.NEW);
                    } else if (doneCount == updateEpic.subtasks.size()) {
                        updateEpic.setStatus(Status.DONE);
                    } else {
                        updateEpic.setStatus(Status.IN_PROGRESS);
                    }
                }
            }
        }

    }

    public Boolean isEmptyCheck() {
        if (this.taskList.isEmpty()) {
            System.out.println("Список задач пуст!");
            return true;
        } else {
            return false;
        }
    }

    public Task selector() {
        while (true) {
            System.out.println("Введите идентификатор:");
            int id = this.scanner.nextInt();
            if (id == 0) {
                System.out.println("Выход");
                return null;
            }

            Task getTask = this.getter(id);
            if (getTask != null) {
                return getTask;
            }

            System.out.println("Неверный идентификатор!");
        }
    }

    public Task getter(int id) {
        if (this.taskList.containsKey(id)) {
            return this.taskList.get(id);
        } else {
            for (Task task : this.taskList.values()) {
                if (task instanceof Epic epic) {
                    if (epic.subtasks.containsKey(id)) {
                        return epic.subtasks.get(id);
                    }
                }
            }

            return null;
        }
    }

    public String typeChecker(Task task) {
        if (task instanceof Epic) {
            return "эпика";
        } else if (task instanceof Subtask) {
            return "подзадачи";
        } else {
            return "задачи";
        }
    }

    public void displayTasks() {
        if (!this.isEmptyCheck()) {
            for (Integer key : this.taskList.keySet()) {
                Task task = this.taskList.get(key);
                String taskType = this.typeChecker(task);
                this.displayTask(task, taskType, false);
                if (task instanceof Epic epic) {

                    for (int subTaskKey : epic.subtasks.keySet()) {
                        Subtask subtask = epic.subtasks.get(subTaskKey);
                        this.displayTask(subtask, "подзадачи", true);
                    }
                }
            }

        }
    }

    public void displayTask(Task task, String type, boolean isBlank) {
        String blank = isBlank ? "    " : "";
        System.out.println(blank + "Id " + type + ": " + task.id);
        System.out.println(blank + "Название " + type + ": " + task.name);
        System.out.println(blank + "Описание " + type + ": " + task.description);
        System.out.println(blank + "Статус " + type + ": " + task.status);
    }

    public void deleteAll() {
        if (!this.isEmptyCheck()) {
            this.taskList.clear();
            this.historyManager.clearHistory();
            System.out.println("Все задачи удалены!");
        }
    }

    public void getById() {
        if (!this.isEmptyCheck()) {
            System.out.println("Получение по идентификатору");
            Task task = this.selector();
            if (task != null) {
                this.displayTask(task, this.typeChecker(task), false);
                this.historyManager.addTaskToHistory(task);
            }

        }
    }

    public void create() {
        while (true) {
            System.out.println("Что вы желаете создать?");
            System.out.println("1 - задача");
            System.out.println("2 - эпик");
            System.out.println("0 - выход");
            switch (this.scanner.next()) {
                case "1":
                    this.taskList.put(++this.id, this.taskCreator(this.id, false));
                    return;
                case "2":
                    this.taskList.put(++this.id, this.epicCreator(this.id, false, true));
                    return;
                case "0":
                    System.out.println("Выход");
                    return;
                default:
                    System.out.println("Неверная команда");
            }
        }
    }

    public Task taskCreator(int taskId, boolean newCheck) {
        String isNew = newCheck ? " новое" : "";
        System.out.println("Введите" + isNew + " название задачи:");
        String taskName = this.scanner.next();
        System.out.println("Введите" + isNew + " описание задачи:");
        String taskDescription = this.scanner.next();
        return new Task(taskId, taskName, taskDescription, Status.NEW);
    }

    public Epic epicCreator(int epicId, boolean newCheck, boolean createSubtasks) {
        String isNew = newCheck ? " новое" : "";
        System.out.println("Введите" + isNew + " название эпика:");
        String epicName = this.scanner.next();
        System.out.println("Введите" + isNew + " описание эпика:");
        String epicDescription = this.scanner.next();
        Epic newEpic = new Epic(epicId, epicName, epicDescription, Status.NEW);
        if (createSubtasks) {
            System.out.println("Сколько подзадач вы желаете добавить?");
            int subTasksCount = this.scanner.nextInt();

            for (int i = 1; i <= subTasksCount; ++i) {
                Subtask newSubtask = this.subTaskCreator(++this.id, i, false);
                newEpic.subtasks.put(this.id, newSubtask);
            }
        }

        return newEpic;
    }

    public Subtask subTaskCreator(int subTaskId, int count, boolean newCheck) {
        String isNew = newCheck ? " новое" : "";
        System.out.println("Подзадача " + count);
        System.out.println("Введите" + isNew + " название подзадачи:");
        String subTaskName = this.scanner.next();
        System.out.println("Введите" + isNew + " описание подзадачи:");
        String subtaskDescription = this.scanner.next();
        return new Subtask(subTaskId, subTaskName, subtaskDescription, Status.NEW);
    }

    public void updateTask() {
        if (!this.isEmptyCheck()) {
            System.out.println("Обновление данных");

            while (true) {
                System.out.println("Выберите старую задачу, которую желаете обновить.");
                Task oldTask = this.selector();
                if (!(oldTask instanceof Subtask)) {
                    if (oldTask != null) {
                        if (oldTask instanceof Epic) {
                            Epic newEpic = this.epicCreator(oldTask.id, true, false);
                            newEpic.subtasks = ((Epic) oldTask).subtasks;
                            this.taskList.replace(oldTask.id, this.taskList.get(oldTask.id), newEpic);
                        } else {
                            this.taskList.replace(oldTask.id, this.taskList.get(oldTask.id), this.taskCreator(oldTask.id, true));
                        }

                        return;
                    }

                    return;
                }

                for (Task task : this.taskList.values()) {
                    if (task instanceof Epic epic) {
                        if (epic.subtasks.containsValue(oldTask)) {
                            Subtask newSubtask = this.subTaskCreator(oldTask.id, 1, true);
                            epic.subtasks.replace(oldTask.id, epic.subtasks.get(oldTask.id), newSubtask);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void updateStatus() {
        if (!this.isEmptyCheck()) {
            System.out.println("Обновление статуса");
            Task task = this.selector();
            if (task != null) {
                String type = this.typeChecker(task);

                while (true) {
                    System.out.println("Введите новый статус " + type + ":");
                    System.out.println("1 - Новый");
                    System.out.println("2 - В процессе");
                    System.out.println("3 - Выполнен");
                    switch (this.scanner.next()) {
                        case "1":
                            task.setStatus(Status.NEW, type);
                            return;
                        case "2":
                            task.setStatus(Status.IN_PROGRESS, type);
                            return;
                        case "3":
                            task.setStatus(Status.DONE, type);
                            return;
                        case "0":
                            System.out.println("Выход");
                            return;
                        default:
                            System.out.println("Неверная команда!");
                    }
                }
            }
        }
    }

    public void deleteTask() {
        if (!this.isEmptyCheck()) {
            System.out.println("Удаление по идентификатору");
            Task task = this.selector();
            if (task instanceof Subtask) {
                for (Task task1 : this.taskList.values()) {
                    if (task1 instanceof Epic epic) {
                        if (epic.subtasks.containsValue(task)) {
                            epic.subtasks.remove(task.id);
                        }
                    }
                }
            } else {
                if (task == null) {
                    return;
                }

                if (task instanceof Epic epic) {
                    for (Subtask subtask : epic.subtasks.values()) {
                        this.historyManager.removeTaskInHistory(subtask.id);
                    }
                }

                this.taskList.remove(task.id);
            }

            this.historyManager.removeTaskInHistory(task.id);
            PrintStream var10000 = System.out;
            String var10001 = this.typeChecker(task);
            var10000.println("Удаление " + var10001 + " успешно выполнено!");
        }
    }
}
