package org.lys.tf.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import sun.net.www.content.audio.basic;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@ServerEndpoint("/bin/{key}")
public class BinServerEndpoint {
    /**
     * 会话映射
     */
    public static final Map<String, Session> SESSIONS_MAP = new ConcurrentHashMap<>();
    public static final Map<String, CtrlData> CTRL_DATA_MAP = new ConcurrentHashMap<>();

    @OnOpen // 用户建立连接时触发。
    public void openSession(@PathParam("key") String key, Session session) {
        SESSIONS_MAP.put(key, session);
        log.info("接收到请求{}", key);
        try {
            CtrlData cd = new CtrlData(key);
            CTRL_DATA_MAP.put(key, cd);
            cd.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage // 监听发送消息的事件
    public void onMessage(@PathParam("key") String key, ByteBuffer data) {
        try {
            CTRL_DATA_MAP.get(key).send(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose // 监听用户断开连接事件
    public void onClose(@PathParam("key") String key, Session session) {
        try {
            SESSIONS_MAP.remove(key);
            CTRL_DATA_MAP.get(key).close();
            CTRL_DATA_MAP.remove(key);
            session.close();
            log.info("关闭连接{}", key);
        } catch (Exception e) {
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

    /**
     * 处理流数据
     */
    class CtrlData extends Thread {
        private String key = null;
        private Socket s = null;
        private InputStream is = null;
        private OutputStream os = null;

        public CtrlData(String key) {
            try {
                this.key = key;
                String ip = key.split(":")[0];
                int port = Integer.parseInt(key.split(":")[1]);
                s = new Socket(ip, port);
                is = s.getInputStream();
                os = s.getOutputStream();
                log.info("连接到{}的{}端口", ip, port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 发送数据
         *
         * @param data
         */
        public void send(ByteBuffer data) {
            try {
                os.write(data.array());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Session session = SESSIONS_MAP.get(key);
                if (session == null) {
                    return;
                }
                RemoteEndpoint.Basic basic = session.getBasicRemote();
                byte[] buf = new byte[2048];
                int len = is.read(buf);
                while (len != -1) {
                    basic.sendBinary(ByteBuffer.wrap(buf, 0, len));
                    len = is.read(buf);
                }
                close();
            } catch (Exception e) {
                log.error("处理流数据出错");
                e.printStackTrace();
            }
        }

        /**
         * 关闭连接
         */
        public void close() throws IOException {
            is.close();
            os.close();
            s.close();
            Session session = SESSIONS_MAP.get(key);
            if (session != null) {
                session.close();
            }
            log.info("断开连接 {}", key);
        }
    }
}
