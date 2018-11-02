package com.clawhub.registrycenter.core;

import com.clawhub.registrycenter.core.netty.NettyServer;
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
     * The Netty server.
     */
    @Autowired
    private NettyServer nettyServer;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("registry-center start!");
        nettyServer.start();
    }
}