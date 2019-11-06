package org.lys.tf.server;

import lombok.extern.slf4j.Slf4j;
import org.lys.tf.security.TFSecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 安全控制Controller
 */
@Slf4j
@RestController
@RequestMapping("/security")
public class SecurityController {

    @Value("${card.key}")
    private String cardKey;

    @Value("${card.data}")
    private String cardData;

    private String[] cardDatas;
    private String[] abc = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    /**
     * 用户登录
     *
     * @param clientInfo 加密后的用户信息
     * @return
     */
    @PostMapping("/")
    public String check(@RequestBody String clientInfo) {
        try {
            TFUtil tFUtil = new TFUtil(new TFSecurityUtil(), null);
            initCard(tFUtil);
            clientInfo = decrypt(tFUtil, clientInfo);

            if (clientInfo.split(",")[0].length() == 13) {
                //验证账号密码
                return checkAccount(tFUtil, clientInfo);
            } else if (clientInfo.split(",").length == 2) {
                //验证密码卡
                return checkCard(tFUtil, clientInfo);
            } else {
                //注册端口
                return regPort(tFUtil, clientInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证账号密码
     *
     * @param tFUtil
     * @param clientInfo
     * @return
     */
    private String checkAccount(TFUtil tFUtil, String clientInfo) throws Exception {
        long time = Long.parseLong(clientInfo.split(",")[0]);
        //验证数据包内时间戳偏差在10分钟内
        if (Math.abs(System.currentTimeMillis() - time) > 600000) {
            return null;
        }
        if (clientInfo.split(",")[1].equals("admin") && clientInfo.split(",")[2].equals("123456")) {
            String token1 = UUID.randomUUID().toString();//生成一级token
            int cardIndex1 = (int) (Math.random() * 100);//生成密码卡序号1
            int cardIndex2 = (int) (Math.random() * 100);//生成密码卡序号2
            BinServerEndpoint.CARD_TOKEN.put(token1, cardDatas[cardIndex1] + cardDatas[cardIndex2]);
            StringBuilder cardIndex = new StringBuilder();
            cardIndex.append(token1);
            cardIndex.append(",");
            cardIndex.append("[");
            cardIndex.append(cardIndex1 / 10 + 1);
            cardIndex.append(abc[cardIndex1 % 10]);
            cardIndex.append("]");
            cardIndex.append("[");
            cardIndex.append(cardIndex2 / 10 + 1);
            cardIndex.append(abc[cardIndex2 % 10]);
            cardIndex.append("]");
            return encrypt(tFUtil, cardIndex.toString());
        }
        return null;
    }

    /**
     * 验证密码卡
     *
     * @param tFUtil
     * @param clientInfo
     * @return
     */
    private String checkCard(TFUtil tFUtil, String clientInfo) throws Exception {
        String token1 = clientInfo.split(",")[0];
        String card = clientInfo.split(",")[1];
        if (card.toUpperCase().equals(BinServerEndpoint.CARD_TOKEN.get(token1))) {
            //密码卡验证通过
            String token = UUID.randomUUID().toString();//生成token
            BinServerEndpoint.CARD_TOKEN.remove(token1);
            BinServerEndpoint.CARD_TOKEN.put(token, token);//通过密码卡验证的token
            return encrypt(tFUtil, token);
        }
        BinServerEndpoint.CARD_TOKEN.remove(token1);
        return null;
    }


    /**
     * 注册端口
     *
     * @param tFUtil
     * @param clientInfo
     * @return
     */
    private String regPort(TFUtil tFUtil, String clientInfo) throws Exception {
        //判断是否通过密码卡验证
        if (!BinServerEndpoint.CARD_TOKEN.containsKey(clientInfo.split(",")[0])) {
            return null;
        }
        BinServerEndpoint.CARD_TOKEN.remove(cardKey.split(",")[0]);

        String token = UUID.randomUUID().toString();//生成token
        byte[] keyByte = tFUtil.getAESKey();//生成ase密钥
        String keyASE = tFUtil.encryptBase64(keyByte);
        ClientInfo cif = new ClientInfo();
        cif.setIp(clientInfo.split(",")[1]);
        cif.setPort(Integer.parseInt(clientInfo.split(",")[2]));
        cif.setKeyByte(keyByte);
        BinServerEndpoint.CLIENT_MAP.put(token, cif);//将token和ase密钥关联
        byte[] re = (token + "," + keyASE).getBytes("utf-8");
        return tFUtil.encryptBase64(tFUtil.encrypt(re, re.length));
    }

    /**
     * 初始化密码卡
     *
     * @param tFUtil
     */
    private void initCard(TFUtil tFUtil) throws Exception {
        //解密密码卡的aesKey
        byte[] aesKey = tFUtil.decryptBase64(cardKey);
        aesKey = tFUtil.decrypt(aesKey, aesKey.length);

        tFUtil.initAES(aesKey);
        //解密密码卡
        byte[] cardDataByte = tFUtil.decryptBase64(cardData);
        String cards = new String(tFUtil.decryptAes(cardDataByte, cardDataByte.length), "utf-8");
        cards = cards.replaceAll(" ", "");
        if (cardDatas == null) {
            cardDatas = cards.split(",");
        }
    }

    /**
     * 加密
     *
     * @param tFUtil
     * @param value
     * @return
     */
    private String encrypt(TFUtil tFUtil, String value) throws Exception {
        byte[] re = value.getBytes("utf-8");
        return tFUtil.encryptBase64(tFUtil.encrypt(re, re.length));
    }

    /**
     * 解密
     *
     * @param tFUtil
     * @param value
     * @return
     */
    private String decrypt(TFUtil tFUtil, String value) throws Exception {
        byte[] userRsaByte = tFUtil.decryptBase64(value);
        return new String(tFUtil.decrypt(userRsaByte, userRsaByte.length), "utf-8");
    }
}
