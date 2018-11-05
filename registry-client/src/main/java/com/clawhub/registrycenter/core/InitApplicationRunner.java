package com.clawhub.registrycenter.core;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.core.netty.NettyTCPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
        try {
            long t0 = System.nanoTime();
            //服务注册
            for (int i = 0; i < 3; i++) {
                JSONObject body = new JSONObject();
                body.put("type", "register");
                body.put("id", String.valueOf(i));
                if (i % 2 == 0) {
                    body.put("role", "consumer");
                } else {
                    body.put("role", "provider");
                }
                body.put("server", "com.clawhub.demo.TcpTest");
                body.put("ip", "192.168.0.1");
                body.put("port", "8080");
                nettyTCPClient.sendMsg(body.toJSONString());
            }

            //服务发现
            JSONObject body1 = new JSONObject();
            body1.put("type", "discover");
            body1.put("server", "com.clawhub.demo.TcpTest");
            nettyTCPClient.sendMsg(body1.toJSONString());


            //心跳

            long t1 = System.nanoTime();
            System.out.println((t1 - t0) / 1000000.0);
            Thread.sleep(5000);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}