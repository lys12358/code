package org.lys.tf.client;

import java.nio.ByteBuffer;

/**
 * 数据回调接口
 */
public interface CallData {
    /**
     * 数据回调
     * @param bytes
     */
    void onMessage(ByteBuffer bytes);

    /**
     * 关闭连接
     */
    void onClose();
}
