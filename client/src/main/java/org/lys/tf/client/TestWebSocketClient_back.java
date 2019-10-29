package org.lys.tf.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;

public class TestWebSocketClient_back extends WebSocketClient {

    Socket s = null;
    InputStream is = null;
    OutputStream os = null;

    public TestWebSocketClient_back(URI serverUri) {
        super(serverUri);
    }

    public TestWebSocketClient_back(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Open a WebSocket connection on client. ");
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        System.out.println("Close a WebSocket connection on client. ");
    }

    @Override
    public void onMessage(String msg) {
        System.out.println("WebSocketClient receives a message: " + msg);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            os.write(bytes.array());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception exception) {
        System.out.println("WebSocketClient exception. " + exception);
        exception.printStackTrace();
    }

    public void start() {
        try {
            ServerSocket ss = new ServerSocket(1022);
            s = ss.accept();
            is = s.getInputStream();
            os = s.getOutputStream();
            System.out.println("获取到网络连接");
            new Thread(new CtrlData()).start();
            this.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理流数据
     */
    class CtrlData implements Runnable {
        public void run() {
            try {
                byte[] buf = new byte[2048];
                int len = is.read(buf);
                while (len != -1) {
                    TestWebSocketClient_back.this.send(ByteBuffer.wrap(buf, 0, len));
                    len = is.read(buf);
                }
            } catch (Exception e) {
                System.out.println("处理流数据出错");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            TestWebSocketClient_back twc = new TestWebSocketClient_back(new URI("ws://localhost:18080/bin/testAddr"));
            twc.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
