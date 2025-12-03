package server.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/epics")) {
                        sendOk(exchange, gson.toJson(manager.getEpics()));
                    } else if (path.matches("/epics/\\d+")) {
                        int id = extractId(path);
                        Epic epic = manager.getEpicById(id);
                        if (epic == null) {
                            sendNotFound(exchange);
                        } else {
                            sendOk(exchange, gson.toJson(epic));
                        }
                    } else if (path.matches("/epics/\\d+/subtasks")) {
                        int epicId = extractId(path);
                        sendOk(exchange, gson.toJson(manager.getSubtasksOfEpic(epicId)));
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic.getId() == 0) {
                        manager.createEpic(epic);
                    } else {
                        manager.updateEpic(epic);
                    }
                    sendCreated(exchange, gson.toJson(epic));
                    break;

                case "DELETE":
                    if (path.matches("/epics/\\d+")) {
                        int id = extractId(path);
                        manager.removeEpicById(id);
                        sendOk(exchange, "{\"message\":\"Эпик удален\"}");
                    }
                    break;

                default:
                    sendText(exchange, "{\"error\":\"Метод не поддерживается\"}", 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "{\"error\":\"Ошибка сервера\"}", 500);
        }
    }
}