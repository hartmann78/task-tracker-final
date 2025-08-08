package service;

import model.Epic;
import model.Node;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    static HashMap<Integer, Node<Task>> history = new HashMap<>();
    private static Node<Task> head;
    private static Node<Task> tail;
    private static int size = 0;

    public void clearHistory() {
        for(Node<Task> taskNode : history.values()) {
            this.removeNode(taskNode);
        }

        history.clear();
    }

    public void check() {
        Node<Task> check = this.checkSize();
        if (check != null) {
            history.remove(check.data.id);
            this.removeNode(check);
        }

    }

    public void addTaskToHistory(Task task) {
        Node<Task> taskNode = this.linkLast(task);
        history.put(task.id, taskNode);
        this.check();
    }

    public List<Task> getHistory() {
        return this.getTasks();
    }

    public void showHistory(List<Task> history) {
        if (history != null && !history.isEmpty()) {
            int count = 1;
            System.out.println("История просмотров задач:");

            for(Task task : history) {
                if (task instanceof Epic) {
                    System.out.println(count + ". getEpic(" + task.id + ")");
                } else if (task instanceof Subtask) {
                    System.out.println(count + ". getSubTask(" + task.id + ")");
                } else {
                    System.out.println(count + ". getTask(" + task.id + ")");
                }

                ++count;
            }
        } else {
            System.out.println("История просмотров пуста.");
        }

    }

    public void removeTaskInHistory(int id) {
        Node<Task> taskNode = history.get(id);
        if (taskNode != null) {
            history.remove(id);
            this.removeNode(taskNode);
        }

    }

    public Node<Task> linkLast(Task element) {
        for(Node<Task> iterator = head; iterator != null; iterator = iterator.next) {
            if (iterator.data == element) {
                this.removeNode(iterator);
                break;
            }
        }

        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        ++size;
        return newNode;
    }

    public Node<Task> checkSize() {
        return this.getSize() > 10 ? head : null;
    }

    public ArrayList<Task> getTasks() {
        if (head == null && tail == null) {
            return null;
        } else {
            ArrayList<Task> tasks = new ArrayList<>();

            Node<Task> oldTail;
            for(oldTail = tail; tail != null; tail = tail.prev) {
                tasks.add(tail.data);
            }

            tail = oldTail;
            return tasks;
        }
    }

    public int getSize() {
        return size;
    }

    public void removeNode(Node<Task> node) {
        Node<Task> next = node.next;
        Node<Task> prev = node.prev;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.data = null;
        --size;
    }
}
