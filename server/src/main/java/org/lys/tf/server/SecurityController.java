package org.lys.tf.server;

import lombok.extern.slf4j.Slf4j;
import org.lys.tf.security.TFSecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 安全控制Controller
 */
@Slf4j
@RestController
@RequestMapping("/security")
public class SecurityController {

    @GetMapping("")
    public String get() {
        return "123";
    }

    /**
     * 用户登录
     *
     * @param clientInfo 加密后的用户信息
     * @return
     */
    @PostMapping("/")
    public String login(@RequestBody String clientInfo) {
        try {
            TFUtil tFUtil = new TFUtil(new TFSecurityUtil(), null);
            byte[] userRsaByte = tFUtil.decryptBase64(clientInfo);
            clientInfo = new String(tFUtil.decrypt(userRsaByte, userRsaByte.length), "utf-8");
            //账号密码认证
            if (clientInfo.startsWith("admin,123456,")) {
                String token = UUID.randomUUID().toString();//生成token
                byte[] keyByte = tFUtil.getAESKey();//生成ase密钥
                String keyASE = tFUtil.encryptBase64(keyByte);
                ClientInfo cif = new ClientInfo();
                cif.setIp(clientInfo.split(",")[2]);
                cif.setPort(Integer.parseInt(clientInfo.split(",")[3]));
                cif.setKeyByte(keyByte);
                BinServerEndpoint.CLIENT_MAP.put(token, cif);//将token和ase密钥关联
                byte[] re = (token + "," + keyASE).getBytes("utf-8");
                return tFUtil.encryptBase64(tFUtil.encrypt(re, re.length));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
