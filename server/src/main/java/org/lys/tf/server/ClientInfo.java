package org.lys.tf.server;

import lombok.Data;

/**
 * 客户端信息
 */
@Data
public class ClientInfo {
    private int port;
    private String ip;
    private byte[] keyByte;
}
