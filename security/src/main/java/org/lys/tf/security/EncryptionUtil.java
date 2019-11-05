package org.lys.tf.security;


import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加解密类
 */
public class EncryptionUtil {

    /**
     * 加密
     *
     * @param data
     * @return
     */
    public byte[] encrypt(byte[] data) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(Config.publicKeyString);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        //加密并返回
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decrypt(byte[] data) throws Exception {
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(Config.privateKeyString);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(data);
    }

    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.getEncoder().encode((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
//        keyMap.put(0, publicKeyString);  //0表示公钥
//        keyMap.put(1, privateKeyString);  //1表示私钥
        System.out.println("publicKeyString: " + publicKeyString);
        System.out.println("privateKeyString: " + privateKeyString);
    }

    public static void main(String[] args) throws Exception {
//        EncryptionUtil eu = new EncryptionUtil();
//        TFSecurityUtil eu1 = new TFSecurityUtil();
//        eu1.setPub(true);
//        eu1.setKey(Config.publicKeyString);
//        eu1.initRas();
//
//        byte[] yuan = "admin,123456,abc123123".getBytes("utf-8");
//        System.out.println(new String(eu1.encrypt(yuan, yuan.length)));


        /*TFSecurityUtil eu2 = new TFSecurityUtil();
        eu2.setPub(false);
        eu2.setKey(Config.privateKeyString);
        eu2.initRas();

        byte[] test = {1, 2, 34, 5, 6, 7, 8, 9, 0};
        System.out.println(Arrays.toString(test));
        byte[] t1 = eu1.encrypt(test, test.length);
        System.out.println(Arrays.toString(t1));
        byte[] t2 = eu2.decrypt(t1, t1.length);
        System.out.println(Arrays.toString(t2));*/
//        eu.genKeyPair();
    }
}
