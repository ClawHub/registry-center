package com.clawhub.registrycenter.util;

import com.clawhub.registrycenter.core.ClientBean;

/**
 * <Description>注册中心key工具类<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 16:04 <br>
 */
public class RegisterKeyUtil {

    public static String getKey(String role, String server, String ip, String port) {
        return role + "_" + server + "_" + ip + "_" + port;
    }

    public static String getKey(ClientBean info) {
        return info.getRole() + "_" + info.getServer() + "_" + info.getIp() + "_" + info.getPort();
    }
}