package org.lys.tf.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加解密工具
 */
public class TFSecurityUtil {
    /**
     * 公钥或私钥
     */
    private String key = null;
    private byte[] keyAes = null;
    /**
     * 是否为公钥
     */
    private boolean isPub = true;

    //加密对象
    private Cipher encryptCipherRsa = null;
    private Cipher encryptCipherAes = null;
    //解密对象
    private Cipher decryptCipherRsa = null;
    private Cipher decryptCipherAes = null;

    public TFSecurityUtil() {
    }

    /**
     * 初始化组件
     */
    public void initRas() {
        try {
            byte[] decoded = Base64.getDecoder().decode(key);
            Key key = null;
            if (isPub) {
                //使用公钥
                key = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            } else {
                //使用私钥
                key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            }
            //RSA加密
            encryptCipherRsa = Cipher.getInstance("RSA");
            encryptCipherRsa.init(Cipher.ENCRYPT_MODE, key);
            //RSA解密
            decryptCipherRsa = Cipher.getInstance("RSA");
            decryptCipherRsa.init(Cipher.DECRYPT_MODE, key);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化AES加解密对象
     *
     * @param key 密钥
     */
    public void initAES(byte[] key) {
        this.keyAes = key;
        try {
            //AES加密
            SecretKeySpec skeySpec = new SecretKeySpec(keyAes, "AES");
            encryptCipherAes = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            encryptCipherAes.init(Cipher.ENCRYPT_MODE, skeySpec);
            //AES解密
            decryptCipherAes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decryptCipherAes.init(Cipher.DECRYPT_MODE, skeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机获取AES密钥
     *
     * @return
     */
    public byte[] getAESKey() {
        try {
            //实例化
            KeyGenerator kgen = null;
            kgen = KeyGenerator.getInstance("AES");
            //设置密钥长度
            kgen.init(128);
            //生成密钥
            SecretKey skey = kgen.generateKey();
            //返回密钥的二进制编码
            return skey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Aes加密
     *
     * @param data
     * @param length
     * @return
     */
    public byte[] encryptAes(byte[] data, int length) throws Exception {
        return encryptCipherAes.doFinal(data, 0, length);
    }

    /**
     * Aes解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decryptAes(byte[] data, int length) throws Exception {
        return decryptCipherAes.doFinal(data, 0, length);
    }

    /**
     * Base64编码
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String encryptBase64(byte[] data) throws Exception {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64解吗
     *
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decryptBase64(String data) throws Exception {
        data = data.replaceAll("%2F", "/");//处理网络传输过程中特殊符号
        return Base64.getDecoder().decode(data);
    }


    /**
     * 加密
     *
     * @param data
     * @param length
     * @return
     */
    public byte[] encrypt(byte[] data, int length) throws Exception {
        return encryptCipherRsa.doFinal(data, 0, length);
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decrypt(byte[] data, int length) throws Exception {
        return decryptCipherRsa.doFinal(data, 0, length);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isPub() {
        return isPub;
    }

    public void setPub(boolean pub) {
        isPub = pub;
    }
}
