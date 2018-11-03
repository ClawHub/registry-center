package com.clawhub.registrycenter.register;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.core.lmdb.LmdbTemplate;
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
     * The Lmdb template.
     */
    @Autowired
    private LmdbTemplate lmdbTemplate;
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
                String message = ConsumerHandler.getConsumerQueue().poll();
                JSONObject body = JSONObject.parseObject(message);
                String server = body.getString("server");
                lmdbTemplate.put(server + "_consumer", message);
            }
        });
        //服务提供者线程
        logger.info("服务提供者线程启动...");
        newFixedThreadPool.submit(() -> {
            while (providerSwitch) {
                String message = ProviderHandler.getProviderQueue().poll();
                JSONObject body = JSONObject.parseObject(message);
                String server = body.getString("server");
                lmdbTemplate.put(server + "_provider", message);
            }
        });
    }

    @PreDestroy
    public void closeThreadPoll() {
        logger.info("注册核心关闭...");
        newFixedThreadPool.shutdownNow();
    }
}