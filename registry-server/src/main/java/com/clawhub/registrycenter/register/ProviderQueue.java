package com.clawhub.registrycenter.register;

import com.clawhub.registrycenter.core.bdb.DataQueue;
import com.clawhub.registrycenter.core.spring.SpringContextHelper;

/**
 * <Description>生产者处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 13:52 <br>
 */
public class ProviderQueue {

    /**
     * The Provider queue.
     */
    private static DataQueue<String> queue = (DataQueue<String>) SpringContextHelper.getBean("providerQueue");

    /**
     * Handle.
     *
     * @param message the message
     */
    public static void handle(String message) {
        queue.generate(message);
    }

    /**
     * Gets queue.
     *
     * @return the queue
     */
    public static DataQueue<String> getQueue() {
        return queue;
    }
}