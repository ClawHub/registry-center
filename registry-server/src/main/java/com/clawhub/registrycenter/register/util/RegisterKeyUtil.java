package com.clawhub.registrycenter.register.util;

/**
 * <Description>注册中心key工具类<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 16:04 <br>
 */
public class RegisterKeyUtil {

    public static String getProviderKey(String server, String ip, String port) {
        return "provider_" + server + "_" + ip + "_" + port;
    }

    public static String getConsumerKey(String server, String ip, String port) {
        return "consumer_" + server + "_" + ip + "_" + port;
    }
}