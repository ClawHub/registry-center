package com.clawhub.registrycenter.core;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.discover.DiscoverHandler;
import com.clawhub.registrycenter.register.ConsumerHandler;
import com.clawhub.registrycenter.register.ProviderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * <Description>客户端消息中转站<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 13:57 <br>
 */
public class MsgDispatcher {
    /**
     * The constant logger.
     */
    private static Logger logger = LoggerFactory.getLogger(MsgDispatcher.class);

    public static String process(String message) {
        logger.info("SERVER接收到消息:" + message);

        if (StringUtils.isEmpty(message)) {
            logger.info("SERVER接收到消息为空");
            return "message is empty!";
        }
        try {
            JSONObject body = JSONObject.parseObject(message);
            String type = body.getString("type");
            if (StringUtils.isEmpty(type)) {
                logger.info("功能类型为空！");
                return "功能类型为空!";
            }
            if ("register".equals(type)) {//服务注册
                logger.info("服务注册...");
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
            } else if ("discover".equals(type)) {//服务发现
                logger.info("服务发现...");
                return DiscoverHandler.handle(message);
            } else {
                return "功能类型不支持!";
            }

        } catch (Exception e) {
            return "message is not json!";
        }
    }

}