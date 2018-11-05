package com.clawhub.registrycenter.core;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.discover.DiscoverHandler;
import com.clawhub.registrycenter.heartbeat.HeartbeatHandler;
import com.clawhub.registrycenter.register.RegisterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

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
                ClientBean info = body.getJSONObject("info").toJavaObject(ClientBean.class);
                return RegisterHandler.handle(info);
            } else if ("discover".equals(type)) {//服务发现
                logger.info("服务发现...");
                String server = body.getString("server");
                return DiscoverHandler.handle(server);
            } else if ("heartbeat".equals(type)) {//心跳
                logger.info("心跳...");
                List<ClientBean> infos = body.getJSONArray("infos").toJavaList(ClientBean.class);
                return HeartbeatHandler.handle(infos);
            } else {
                return "功能类型不支持!";
            }

        } catch (Exception e) {
            return "message is not json!";
        }
    }

}