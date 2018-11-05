package com.clawhub.registrycenter.util;

import com.clawhub.registrycenter.client.ClientBean;
import com.clawhub.registrycenter.constant.ParamConstant;

/**
 * <Description>注册中心key工具类<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 16:04 <br>
 */
public class RegisterKeyUtil {

    /**
     * Gets key.
     *
     * @param role   the role
     * @param server the service
     * @param ip     the ip
     * @param port   the port
     * @return the key
     */
    public static String getKey(String role, String server, String ip, String port) {
        return role + ParamConstant.UNDER_LINE + server + ParamConstant.UNDER_LINE + ip + ParamConstant.UNDER_LINE + port;
    }

    /**
     * Gets key.
     *
     * @param info the info
     * @return the key
     */
    public static String getKey(ClientBean info) {
        return info.getRole() + "_" + info.getServer() + "_" + info.getIp() + "_" + info.getPort();
    }
}
