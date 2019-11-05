package org.lys.tf.client;

import org.lys.tf.security.Config;
import org.lys.tf.security.TFSecurityUtil;

import java.nio.ByteBuffer;

public class TFUtil {
    private TFSecurityUtil tFSecurityUtil;

    public TFUtil(TFSecurityUtil tFSecurityUtil, String aesKey) {
        try {
            this.tFSecurityUtil = tFSecurityUtil;
            if (tFSecurityUtil != null) {
                tFSecurityUtil.setKey(Config.publicKeyString);
                tFSecurityUtil.setPub(true);
                tFSecurityUtil.initRas();
                tFSecurityUtil.initAES(tFSecurityUtil.decryptBase64(aesKey));
                System.out.println("初始化加密解密工具");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            System.out.println("解密错误");
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
            System.out.println("加密错误");
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
