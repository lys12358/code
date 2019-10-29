package org.lys.tf.client;

import java.util.HashMap;
import java.util.Map;

public class TFClientMain {
    private Map<Integer, String> portMap;

    public TFClientMain() {
        portMap = new HashMap<Integer, String>();
        portMap.put(1022, "192.168.100.100:22");
        portMap.put(1006, "192.168.100.100:3306");
        portMap.put(1007, "107.148.246.224:19208");
    }

    public void start() {
        System.out.println("启动TF客户端");
        System.out.println("映射地址");
        portMap.forEach((port, addr) -> {
            new Thread(new TFPortThread(port, addr)).start();
            System.out.println(port + " => " + addr);
        });
    }

    public static void main(String[] args) {
        TFClientMain tfc = new TFClientMain();
        tfc.start();
    }
}
