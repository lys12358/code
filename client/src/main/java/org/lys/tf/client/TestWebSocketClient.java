package org.lys.tf.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;

public class TestWebSocketClient extends WebSocketClient {

    Socket s = null;
    InputStream is = null;
    OutputStream os = null;
    CallData cd = null;

    public TestWebSocketClient(CallData cd, URI serverUri) {
        super(serverUri);
        this.cd = cd;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        cd.onClose();
    }

    @Override
    public void onMessage(String msg) {
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        cd.onMessage(bytes);
    }

    @Override
    public void onError(Exception exception) {
        System.out.println("WebSocketClient exception. " + exception);
        exception.printStackTrace();
    }

}
