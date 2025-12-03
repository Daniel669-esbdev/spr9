package server.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Save;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/tasks")) {
                        sendOk(exchange, gson.toJson(manager.getTasks()));
                    } else if (path.matches("/tasks/\\d+")) {
                        int id = extractId(path);
                        Save task = manager.getTaskById(id);
                        if (task == null) {
                            sendNotFound(exchange);
                        } else {
                            sendOk(exchange, gson.toJson(task));
                        }
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Save task = gson.fromJson(body, Save.class);
                    if (task.getId() == 0) {
                        manager.createTask(task);
                    } else {
                        manager.updateTask(task);
                    }
                    sendCreated(exchange, gson.toJson(task));
                    break;

                case "DELETE":
                    if (path.matches("/tasks/\\d+")) {
                        int id = extractId(path);
                        manager.removeTaskById(id);
                        sendOk(exchange, "{\"message\":\"Задача удалена\"}");
                    } else {
                        manager.clearAllTasks();
                        sendOk(exchange, "{\"message\":\"Все задачи удалены\"}");
                    }
                    break;

                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Ошибка сервера\"}", 500);
            }
    }
}