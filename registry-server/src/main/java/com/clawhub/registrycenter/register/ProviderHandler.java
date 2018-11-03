package com.clawhub.registrycenter.register;

import com.clawhub.registrycenter.core.spring.SpringContextHelper;
import com.clawhub.registrycenter.core.bdb.DataQueue;

/**
 * <Description>生产者处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 13:52 <br>
 */
public class ProviderHandler {

    /**
     * The Provider queue.
     */
    private static DataQueue<String> providerQueue = (DataQueue<String>) SpringContextHelper.getBean("providerQueueDbDir");

    /**
     * Handle.
     *
     * @param message the message
     */
    public static void handle(String message) {
        providerQueue.generate(message);
    }

    /**
     * Gets provider queue.
     *
     * @return the provider queue
     */
    public static DataQueue<String> getProviderQueue() {
        return providerQueue;
    }
}