package com.clawhub.registrycenter.register;

import com.clawhub.registrycenter.core.SpringContextHelper;
import com.clawhub.registrycenter.core.bdb.DataQueue;

/**
 * <Description>消费者处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 13:53 <br>
 */
public class ConsumerHandler {
    /**
     * The Consumer queue.
     */
    private static DataQueue<String> consumerQueue = (DataQueue<String>) SpringContextHelper.getBean("consumerQueueDbDir");

    /**
     * Handle.
     *
     * @param message the message
     */
    public static void handle(String message) {
        consumerQueue.generate(message);
    }

    /**
     * Gets consumer queue.
     *
     * @return the consumer queue
     */
    public static DataQueue<String> getConsumerQueue() {
        return consumerQueue;
    }
}