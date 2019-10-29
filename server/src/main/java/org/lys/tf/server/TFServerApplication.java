package org.lys.tf.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@EnableWebSocket //开启WebSocket
@SpringBootApplication
public class TFServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TFServerApplication.class, args);
    }

    @Bean //注入Spring容器的bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
