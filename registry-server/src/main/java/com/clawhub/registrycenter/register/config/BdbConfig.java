package com.clawhub.registrycenter.register.config;

import com.clawhub.registrycenter.core.bdb.DataQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <Description>BDB配置<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 14:43 <br>
 */
@Configuration
public class BdbConfig {
    /**
     * The Provider queue db dir.
     */
    @Value("${bdb.provider.queue.db.dir}")
    private String providerQueueDbDir;

    /**
     * The Consumer queue db dir.
     */
    @Value("${bdb.consumer.queue.db.dir}")
    private String consumerQueueDbDir;

    /**
     * Description: 供应商数据队列 <br>
     *
     * @return data queue
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @Bean(name = "providerQueue")
    public DataQueue<String> providerQueueDbDir() {
        return new DataQueue<>(String.class, providerQueueDbDir, "provider");
    }

    /**
     * Description: 客户数据队列 <br>
     *
     * @return data queue
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @Bean(name = "customerQueue")
    public DataQueue<String> customerDataQueue() {
        return new DataQueue<>(String.class, consumerQueueDbDir, "consumer");
    }

}
