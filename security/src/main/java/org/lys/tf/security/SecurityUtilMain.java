package org.lys.tf.security;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 安全工具入口
 */
public class SecurityUtilMain {

    /**
     * 生成密码卡
     */
    public void makeCard() throws Exception {
        String[] data = new String[100];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                data[i * 10 + j] = getRandomChar();
                System.out.print(data[i * 10 + j]);
                System.out.print("\t");
            }
            System.out.println();
        }

        String str = Arrays.toString(data);
        str = str.substring(1, str.length() - 1);
        System.out.println(str);

        TFSecurityUtil eu = new TFSecurityUtil();
        //使用公钥加密
        eu.setPub(true);
        eu.setKey(Config.publicKeyString);
        eu.initRas();

        byte[] aesKey = eu.getAESKey();
        eu.initAES(aesKey);

        byte[] byteStr = str.getBytes("utf-8");
        System.out.println(eu.encryptBase64(eu.encrypt(aesKey, aesKey.length)));
        System.out.println(eu.encryptBase64(eu.encryptAes(byteStr, byteStr.length)));
    }

    /**
     * 随机返回2个字符
     *
     * @return
     */
    private String getRandomChar() {
        char c1 = (char) (Math.random() * 10 + 48);
        char c2 = (char) (Math.random() * 26 + 65);
        return String.valueOf(c1) + String.valueOf(c2);
    }

    public static void main(String[] args) throws Exception {
//        EncryptionUtil eu = new EncryptionUtil();
        SecurityUtilMain sum = new SecurityUtilMain();
        sum.makeCard();

    }
}
