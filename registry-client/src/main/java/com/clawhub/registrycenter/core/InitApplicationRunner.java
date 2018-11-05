package com.clawhub.registrycenter.core;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.constant.ParamConstant;
import com.clawhub.registrycenter.core.netty.NettyTCPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <Description>系统启动后执行<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/2 17:19 <br>
 */
@Component//被spring容器管理
@Order(1)//如果多个自定义ApplicationRunner，用来标明执行顺序
public class InitApplicationRunner implements ApplicationRunner {
    /**
     * The Logger.
     */
    private Logger logger = LoggerFactory.getLogger(InitApplicationRunner.class);
    /**
     * The Netty tcp client.
     */
    @Autowired
    private NettyTCPClient nettyTCPClient;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("TCP客户端初始化...");
        nettyTCPClient.init();

        logger.info("TCP客户端测试...");
        List<ClientBean> clientBeans = new ArrayList<>();
        try {
            //服务注册
            logger.info("服务注册测试开始...");
            for (int i = 0; i < 10; i++) {
                JSONObject register = new JSONObject();
                register.put("type", ParamConstant.TYPE_REGISTER);
                ClientBean clientBean = new ClientBean();
                if (i % 2 == 0) {
                    clientBean.setRole(ParamConstant.ROLE_CONSUMER);
                } else {
                    clientBean.setRole(ParamConstant.ROLE_PROVIDER);
                }
                clientBean.setServer("com.clawhub.demo.TcpTest");
                clientBean.setIp("192.168.0.1");
                clientBean.setPort("8080");
                clientBean.setActive(ParamConstant.ACTIVE);
                clientBeans.add(clientBean);
                register.put("info", JSONObject.toJSONString(clientBean));
                nettyTCPClient.sendMsg(register.toJSONString());
                logger.info("服务注册测试结束...");
            }

            //服务发现
            logger.info("服务发现测试开始...");
            JSONObject discover = new JSONObject();
            discover.put("type", ParamConstant.TYPE_DISCOVER);
            discover.put("server", "com.clawhub.demo.TcpTest");
            nettyTCPClient.sendMsg(discover.toJSONString());
            logger.info("服务发现测试结束...");

            //心跳
            logger.info("心跳测试开始...");
            for (int i = 0; i < 10; i++) {
                logger.info("第 {} 心跳", i);
                JSONObject heartbeat = new JSONObject();
                heartbeat.put("type", ParamConstant.TYPE_HEARTBEAT);
                heartbeat.put("infos", JSONObject.toJSONString(clientBeans));
                nettyTCPClient.sendMsg(heartbeat.toJSONString());
                Thread.sleep(3000);
            }
            logger.info("心跳测试结束...");
        } catch (Exception e) {
            logger.error("TCP客户端出错...", e);
        }
    }
}
