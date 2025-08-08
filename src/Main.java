import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import utils.Managers;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String savePath = "C:\\Users\\Lenovo\\Desktop\\Java\\JavaProjects\\task_tracker\\src\\save.csv";
        InMemoryTaskManager taskManager = Managers.loadFromFile(savePath);
        System.out.println("Сохранение успешно загружено!\n");

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        System.out.println("Трекер задач");

        while (true) {
            taskManager.returnId();
            taskManager.epicStatusCheck();
            taskManager.showFunctions();
            switch (scanner.next()) {
                case "1":
                    taskManager.displayTasks();
                    break;
                case "2":
                    taskManager.deleteAll();
                    break;
                case "3":
                    taskManager.getById();
                    break;
                case "4":
                    taskManager.create();
                    break;
                case "5":
                    taskManager.updateTask();
                    break;
                case "6":
                    taskManager.updateStatus();
                    break;
                case "7":
                    taskManager.deleteTask();
                    break;
                case "8":
                    historyManager.showHistory(historyManager.getHistory());
                    break;
                case "9":
                    System.out.println("Завершение...");
                    return;
                default:
                    System.out.println("Такая команда отсутствует!");
            }
        }
    }
}
