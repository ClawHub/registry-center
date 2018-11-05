package com.clawhub.registrycenter.register;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.core.MsgDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * <Description>注册处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/5 13:43 <br>
 */
public class RegisterHandler {
    /**
     * The constant logger.
     */
    private static Logger logger = LoggerFactory.getLogger(MsgDispatcher.class);

    /**
     * 服务注册
     *
     * @param message the message
     * @return the string
     */
    public static String handle(String message) {
        JSONObject body = JSONObject.parseObject(message);
        String role = body.getString("role");
        String id = body.getString("id");
        String server = body.getString("server");
        String ip = body.getString("ip");
        String port = body.getString("port");
        if (StringUtils.isEmpty(server) || StringUtils.isEmpty(ip) || StringUtils.isEmpty(port)) {
            return "服务信息为空！";
        }
        if ("provider".equals(role)) {
            //服务提供者注册
            logger.info("服务提供者注册...");
            ProviderHandler.handle(message);
            return id + ": provider register success!";
        } else if ("consumer".equals(role)) {
            //服务消费者注册
            logger.info("服务消费者注册...");
            ConsumerHandler.handle(message);
            return id + ": consumer register success!";
        } else {
            logger.info("服务注册类型不支持!");
            return "服务注册类型不支持!";
        }
    }
}