package com.mxsky.dbbackup.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/logsWS/{userId}")
@Component(value="logsWS")
public class LogsWS {

    private static final Logger log = LoggerFactory.getLogger(LogsWS.class);
    private static ConcurrentHashMap<String, Session> webSocketMap = new ConcurrentHashMap<>();
    private Session session;
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, session);
        } else {
            webSocketMap.put(userId, session);
        }
    }


    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
    }


    public void sendMessage(String message) throws IOException {
        for (Map.Entry<String, Session> item : webSocketMap.entrySet()) {
            try {
                item.getValue().getBasicRemote().sendText(message);
            } catch (Exception e) {
            }
        }
    }


}
