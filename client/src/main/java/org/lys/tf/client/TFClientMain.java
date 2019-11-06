package org.lys.tf.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lys.tf.security.Config;
import org.lys.tf.security.TFSecurityUtil;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TFClientMain {
    private Map<Integer, String> portMap;

    public TFClientMain() {
        portMap = new HashMap<Integer, String>();
        portMap.put(1022, "123.57.243.78:7003");
//        portMap.put(1022, "192.168.1.71:22");
//        portMap.put(1022, "192.168.100.100:22");
//        portMap.put(1006, "192.168.100.100:3306");
//        portMap.put(1007, "107.148.246.224:19208");
    }

    public void start() {
        System.out.println("启动TF客户端");
        System.out.println("正在登录到服务端…");
        Map<Integer, String> tokenMap = null;
        while (true) {
            tokenMap = login();
            if (tokenMap == null) {
                System.out.println("登录到服务端失败!");
                continue;
            }
            break;
        }
        System.out.println("成功登录到服务端");
        System.out.println("映射地址");
        Map<Integer, String> finalTokenMap = tokenMap;
        portMap.forEach((port, addr) -> {
            new Thread(new TFPortThread(port, addr, finalTokenMap.get(port))).start();
            System.out.println(port + " => " + addr);
        });
    }

    /**
     * 登录服务端
     *
     * @return 返回每一个端口的token和密钥
     */
    private Map<Integer, String> login() {
        try {
            Map<Integer, String> tokenMap = new HashMap<Integer, String>();
            Console console = System.console();//开发环境下无法获取控制台操作
            Scanner sc = null;
            if (console == null) {
                sc = new Scanner(System.in);
            }
            System.out.println("请输入用户名");
            String account = console == null ? sc.nextLine() : console.readLine();
            System.out.println("请输入密码");
            String password = console == null ? sc.nextLine() : new String(console.readPassword());

            System.out.println("登录用户 " + account);

            TFSecurityUtil eu = new TFSecurityUtil();
            eu.setPub(true);
            eu.setKey(Config.publicKeyString);
            eu.initRas();

            //验证账号密码
            String info = System.currentTimeMillis() + "," + account + "," + password;
            String rs = sendPost(eu, info);
            if (rs == null) {
                return null;
            }

            //验证密码卡
            System.out.println("请输入密码卡编码 " + rs.split(",")[1]);
            String card = console == null ? sc.nextLine() : new String(console.readPassword());
            info = rs.split(",")[0] + "," + card;
            rs = sendPost(eu, info);
            if (rs == null) {
                return null;
            }

            //注册每一个端口映射
            String token = rs;
            portMap.forEach((port, addr) -> {
                try {
                    String value = token + "," + addr.split(":")[0] + "," + addr.split(":")[1];
                    tokenMap.put(port, sendPost(eu, value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return tokenMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提交POST请求
     *
     * @param eu
     * @param value
     * @return
     */
    private String sendPost(TFSecurityUtil eu, String value) throws Exception {
        byte[] yuan = value.getBytes("utf-8");
        String base64 = eu.encryptBase64(eu.encrypt(yuan, yuan.length));
        StringEntity stringEntity = new StringEntity(base64);
        stringEntity.setContentType("application/x-www-form-urlencoded; charset=utf-8");
        HttpPost httpPost = new HttpPost("http://127.0.0.1:18080/security/");
        httpPost.setEntity(stringEntity);

        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String data = EntityUtils.toString(entity);
            if (data == null) {
                return null;
            }
            byte[] data1 = eu.decryptBase64(data);
            return new String(eu.decrypt(data1, data1.length), "utf-8");
        }
        return null;
    }

    public static void main(String[] args) {
        TFClientMain tfc = new TFClientMain();
        tfc.start();

        /*try {
            TFSecurityUtil eu1 = new TFSecurityUtil();
            eu1.setPub(true);
            eu1.setKey(Config.publicKeyString);
            eu1.initRas();
            byte[] yuan = "admin,123456,abc123123".getBytes("utf-8");
            String userRsa = Base64.getEncoder().encodeToString(eu1.encrypt(yuan, yuan.length));
            byte[] userRsaByte = Base64.getDecoder().decode(userRsa);
            System.out.println(userRsaByte.length);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*try {
            TFSecurityUtil eu1 = new TFSecurityUtil();
            eu1.setPub(true);
            eu1.setKey(Config.publicKeyString);
            eu1.initRas();

            byte[] yuan = "admin,123456,abc123123".getBytes("utf-8");
            String base64 = eu1.encryptBase64(eu1.encrypt(yuan, yuan.length));
            StringEntity stringEntity = new StringEntity(base64);
            stringEntity.setContentType("application/x-www-form-urlencoded; charset=utf-8");
            HttpPost httpPost = new HttpPost("http://127.0.0.1:18080/security/");
            httpPost.setEntity(stringEntity);

            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String data = EntityUtils.toString(entity);
                byte[] data1 = eu1.decryptBase64(data);
                System.out.println(eu1.decrypt(data1, data1.length));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }
}
