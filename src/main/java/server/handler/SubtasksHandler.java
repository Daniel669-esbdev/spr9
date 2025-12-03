package server.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/subtasks")) {
                        sendOk(exchange, gson.toJson(manager.getSubtasks()));
                    } else if (path.matches("/subtasks/\\d+")) {
                        int id = extractId(path);
                        Subtask subtask = manager.getSubtaskById(id);
                        if (subtask == null) {
                            sendNotFound(exchange);
                        } else {
                            sendOk(exchange, gson.toJson(subtask));
                        }
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (subtask.getId() == 0) {
                        manager.createSubtask(subtask);
                    } else {
                        manager.updateSubtask(subtask);
                    }
                    sendCreated(exchange, gson.toJson(subtask));
                    break;

                case "DELETE":
                    if (path.matches("/subtasks/\\d+")) {
                        int id = extractId(path);
                        manager.removeSubtaskById(id);
                        sendOk(exchange, "{\"message\":\"Удалил подзадачу\"}");
                    } else {
                        manager.clearAllSubtasks();
                        sendOk(exchange, "{\"message\":\"Удалил все подзадачи\"}");
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