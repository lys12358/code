package org.lys.tf.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * 端口监听线程
 */
public class TFPortThread implements Runnable {
    private final String REM_ADDR = "ws://localhost:18080/bin/";
    private int port;
    private String addr;
    private boolean run = true;

    public TFPortThread(int port, String addr) {
        this.port = port;
        this.addr = addr;
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(this.port);
            while (run) {
                Socket s = ss.accept();
                new Thread(new CtrlConn(s)).start();
            }
        } catch (Exception e) {
            System.out.println("端口监听出错");
            e.printStackTrace();
        }
    }

    /**
     * 端口连接处理
     */
    class CtrlConn implements Runnable, CallData {
        Socket s = null;
        InputStream is = null;
        OutputStream os = null;
        TestWebSocketClient twc = null;
        String uri = null;

        public CtrlConn(Socket s) {
            try {
                this.s = s;
                is = s.getInputStream();
                os = s.getOutputStream();
                uri = REM_ADDR + addr + ":" + UUID.randomUUID();
                twc = new TestWebSocketClient(this, new URI(uri));
                twc.connect();
                System.out.println("已连接" + uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 数据接收回调函数
         *
         * @param bytes
         */
        @Override
        public void onMessage(ByteBuffer bytes) {
            try {
                os.write(bytes.array());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 关闭连接回调函数
         */
        @Override
        public void onClose() {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                byte[] buf = new byte[2048];
                int len = is.read(buf);
                while (len != -1) {
                    twc.send(ByteBuffer.wrap(buf, 0, len));
                    len = is.read(buf);
                }
                close();
            } catch (Exception e) {
                if(!s.isClosed()){
                    e.printStackTrace();
                }
            }
        }

        private void close() throws IOException {
            is.close();
            os.close();
            s.close();
            twc.close();
            System.out.println("连接已关闭" + uri);
        }


    }
}
