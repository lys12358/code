package org.lys.tf.security;

/**
 * 安全配置类
 */
public class Config {
    static {
        Config.keyString = Config.privateKeyString;
    }

    /**
     * 确定使用公钥或私钥使用
     */
    public static String keyString;
    public static String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCR0rWYBHkHNTMAySMZPqwCtowOiYE3RbUXARa6TgRnngNU/IyiDZiZ3z3EsQ45xvPj7snWIBuqgBuQ66477jTngulqoxoKSAb7kEpZKNDhOEYtKH8m6OjLhNxMWKHX1/hbt7xMx1Ique9J4L2JLkMxROf8DVPz0idOigWjXEYLJQIDAQAB";
    public static String privateKeyString = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJHStZgEeQc1MwDJIxk+rAK2jA6JgTdFtRcBFrpOBGeeA1T8jKINmJnfPcSxDjnG8+PuydYgG6qAG5DrrjvuNOeC6WqjGgpIBvuQSlko0OE4Ri0ofybo6MuE3ExYodfX+Fu3vEzHUiq570ngvYkuQzFE5/wNU/PSJ06KBaNcRgslAgMBAAECgYAX4IOhh6H1ny1hxI19tbhenVtjifphfYWAZco96UZpXQM4qD2yzz0cfORVT5R/u2b1/ISKDinZii55VU3rPD7nJkHWXO1tHqPxUfNYcIKVnkbSnmYa9ITxVqKFuEWMLLT+dhiKXEbHN4J6qON+cmeZvvl6rMg5MT/HDGV/cs8+oQJBAMlC7txQaKXALlQmiC4HxXoBDpbpQS0IZpiDBDB+rpY5FL8UMIIla/SN4ZVndanDlEeeHLGbvwR/36zSNteX3r0CQQC5e9BgPDYQqEdbYCOe5Ch8WFN57UYfHtQ+9z+lIxQ1rAV11GZ9r8ZsBdHZTOI1muzGP0PPZnnOzMLDCqI2CLiJAkAy7DKPn/C6b4PJWu595w16LyZH3MDW6hc7Y3r94i++ZrbvGeakJ1/HQtbe/Ixy5b9w13qfHa8ZLlmj2pRxvEVpAkEAqOpjqrBKwEKvsTxNVC8N26eIZdo6aK7ntWvvTxEyH2rIEjVbHQ0Nk2R9ZfpJa23qQORueZsmRLleiMbPUIwCmQJABVw2koOaMHsMo7D7HO3RTLYW/wyYuq2nx3Y2X5RoYCJ4kJiqctsJjOKKsFZI4TKndlC1vYQ39rn7ixXtPNAGTQ==";
}
