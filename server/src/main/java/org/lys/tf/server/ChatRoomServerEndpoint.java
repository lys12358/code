package org.lys.tf.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@ServerEndpoint("/chat-room/{username}")
public class ChatRoomServerEndpoint {

    public static final Map<String, Session> ONLINE_USER_SESSIONS = new ConcurrentHashMap<>();

    public static void sendMessage(Session session, String message) {
        if (session == null) {
            return;
        }

        final RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            return;
        }
        try {
            basic.sendText(message);
        } catch (IOException e) {
            log.error("sendMessage IOException ", e);
        }
    }

    public static void sendMessageAll(String message) {
        ONLINE_USER_SESSIONS.forEach((sessionId, session) -> sendMessage(session, message));
    }

    @OnOpen // 用户建立连接时触发。
    public void openSession(@PathParam("username") String username, Session session) {
        ONLINE_USER_SESSIONS.put(username, session);
        String message = "欢迎用户[" + username + "] 来到聊天室！";
        log.info("用户登录：{}", message);
        sendMessageAll(message);
    }

    @OnMessage // 监听发送消息的事件
    public void onMessage(@PathParam("username") String username, String message) {
        log.info("用户[{}]发送消息：{}", username, message);
        sendMessageAll("用户[" + username + "] : " + message);
    }

    @OnClose // 监听用户断开连接事件
    public void onClose(@PathParam("username") String username, Session session) {
        // 当前的Session 移除
        ONLINE_USER_SESSIONS.remove(username);
        // 并且通知其他人当前用户已经离开聊天室了
        sendMessageAll("用户[" + username + "] 已经离开聊天室了！");
        try {
            session.close();
        } catch (IOException e) {
            log.error("onClose error{}", e);
        }
    }

    @OnError //监听错误事件
    public void onError(Session session, Throwable throwable) {
        try {
            session.close();
        } catch (IOException e) {
            log.error("onError excepiton,{}", e);
        }
        log.info("Throwable msg ,{}", throwable.getMessage());
    }
}