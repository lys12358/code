package org.lys.tf.server;

import lombok.extern.slf4j.Slf4j;
import org.lys.tf.security.Config;
import org.lys.tf.security.TFSecurityUtil;

import java.nio.ByteBuffer;

@Slf4j
public class TFUtil {
    //    @Autowired
    private TFSecurityUtil tFSecurityUtil;

    public TFUtil(TFSecurityUtil tFSecurityUtil, byte[] aesKey) {
        this.tFSecurityUtil = tFSecurityUtil;
        if (tFSecurityUtil != null) {
            tFSecurityUtil.setKey(Config.privateKeyString);
            tFSecurityUtil.setPub(false);
            tFSecurityUtil.initRas();
            if (aesKey != null) {
                tFSecurityUtil.initAES(aesKey);
            }
            log.info("初始化加密解密工具");
        }
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decrypt(byte[] data, int length) {
        try {
            return tFSecurityUtil.decrypt(data, length);
        } catch (Exception e) {
            log.error("解密错误", e);
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 解密
     *
     * @param data
     * @return
     */
    public byte[] decrypt(ByteBuffer data) {
        return decrypt(data.array(), data.limit());
    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
    public byte[] encrypt(byte[] data, int length) {
        try {
            return tFSecurityUtil.encrypt(data, length);
        } catch (Exception e) {
            log.error("加密错误", e);
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
    public byte[] encrypt(ByteBuffer data) {
        return encrypt(data.array(), data.limit());
    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
    public ByteBuffer encryptBuffer(byte[] data, int length) {
        return ByteBuffer.wrap(encrypt(data, length));
    }

    /**
     * 随机获取AES密钥
     *
     * @return
     */
    public byte[] getAESKey() {
        return tFSecurityUtil.getAESKey();
    }

    /**
     * Base64编码
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String encryptBase64(byte[] data) throws Exception {
        return tFSecurityUtil.encryptBase64(data);
    }

    /**
     * Base64解吗
     *
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decryptBase64(String data) throws Exception {
        return tFSecurityUtil.decryptBase64(data);
    }

    /**
     * Aes加密
     *
     * @param data
     * @param length
     * @return
     */
    public byte[] encryptAes(byte[] data, int length) throws Exception {
        return tFSecurityUtil.encryptAes(data, length);
    }

    /**
     * Aes解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] decryptAes(byte[] data, int length) throws Exception {
        return tFSecurityUtil.decryptAes(data, length);
    }
}
