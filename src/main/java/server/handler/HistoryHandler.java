package server.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Save;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    if (path.equals("/history")) {
                        sendOk(exchange, gson.toJson(manager.getHistory()));
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "DELETE":
                    if (path.equals("/history")) {
                        sendOk(exchange, "{\"message\":\"История очищена\"}");
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendText(exchange, "{\"error\":\"Метод не поддерживается\"}", 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "{\"error\":\"Внутренняя ошибка сервера\"}", 500);
        }
    }
}