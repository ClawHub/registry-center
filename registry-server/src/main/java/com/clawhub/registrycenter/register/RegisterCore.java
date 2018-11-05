package com.clawhub.registrycenter.register;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.client.ClientBean;
import com.clawhub.registrycenter.client.ClientPool;
import com.clawhub.registrycenter.util.RegisterKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <Description>注册核心<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 14:58 <br>
 */
@Component
public class RegisterCore {

    /**
     * The Logger.
     */
    private Logger logger = LoggerFactory.getLogger(RegisterCore.class);
    /**
     * The Consumer switch.
     */
    @Value("${register.consumer.queue.poll.switch}")
    private Boolean consumerSwitch;
    /**
     * The Provider switch.
     */
    @Value("${register.provider.queue.poll.switch}")
    private Boolean providerSwitch;


    /**
     * The Client pool.
     */
    @Autowired
    private ClientPool clientPool;
    /**
     * The New fixed thread pool.
     */
    private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        logger.info("注册核心启动...");
        //服务消费者线程
        logger.info("服务消费者线程启动...");
        newFixedThreadPool.submit(() -> {
            while (consumerSwitch) {
                String message = ConsumerQueue.getQueue().poll();
                ClientBean clientBean = JSONObject.parseObject(message, ClientBean.class);
                String ip = clientBean.getIp();
                String port = clientBean.getPort();
                String server = clientBean.getServer();
                String role = clientBean.getRole();
                logger.info("角色：{}，服务: {} ,IP: {} ,端口: {} ,订阅成功", role, server, ip, port);
                String consumerKey = RegisterKeyUtil.getKey(role, server, ip, port);
                clientPool.register(consumerKey, JSONObject.toJSONString(clientBean));
            }
        });
        //服务提供者线程
        logger.info("服务提供者线程启动...");
        newFixedThreadPool.submit(() -> {
            while (providerSwitch) {
                String message = ProviderQueue.getQueue().poll();
                ClientBean clientBean = JSONObject.parseObject(message, ClientBean.class);
                String ip = clientBean.getIp();
                String port = clientBean.getPort();
                String server = clientBean.getServer();
                String role = clientBean.getRole();
                logger.info("角色：{}，服务: {} ,IP: {} ,端口: {} ,注册成功", role, server, ip, port);
                String providerKey = RegisterKeyUtil.getKey(role, server, ip, port);
                clientPool.register(providerKey, JSONObject.toJSONString(clientBean));
            }
        });
    }

    /**
     * Close thread poll.
     */
    @PreDestroy
    public void closeThreadPoll() {
        logger.info("注册核心关闭...");
        newFixedThreadPool.shutdownNow();
    }
}
