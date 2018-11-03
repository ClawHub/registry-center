package com.clawhub.registrycenter.register;

import com.alibaba.fastjson.JSONObject;
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
            return "message is empty!";
        }
        try {
            JSONObject body = JSONObject.parseObject(message);
            String type = body.getString("type");
            String id = body.getString("id");
            String server = body.getString("server");
            String ip = body.getString("ip");
            String port = body.getString("port");
            if (StringUtils.isEmpty(server) || StringUtils.isEmpty(ip) || StringUtils.isEmpty(port)) {
                return "server info is empty!";
            }
            if ("provider".equals(type)) {
                //服务提供者注册
                ProviderHandler.handle(message);
                return id + ": provider register success!";
            } else if ("consumer".equals(type)) {
                //服务消费者注册
                ConsumerHandler.handle(message);
                return id + ": consumer register success!";
            } else {
                return "type is not accept!";
            }
        } catch (Exception e) {
            return "message is not json!";
        }
    }

}