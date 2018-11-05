package com.clawhub.registrycenter.register;

import com.clawhub.registrycenter.core.bdb.DataQueue;
import com.clawhub.registrycenter.core.spring.SpringContextHelper;

/**
 * <Description>消费者处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 13:53 <br>
 */
public class ConsumerQueue {
    /**
     * The Consumer queue.
     */
    private static DataQueue<String> consumer = (DataQueue<String>) SpringContextHelper.getBean("customerQueue");

    /**
     * Handle.
     *
     * @param message the message
     */
    public static void handle(String message) {
        consumer.generate(message);
    }

    /**
     * Gets queue.
     *
     * @return the queue
     */
    public static DataQueue<String> getQueue() {
        return consumer;
    }
}