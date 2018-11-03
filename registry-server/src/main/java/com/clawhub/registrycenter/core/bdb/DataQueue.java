package com.clawhub.registrycenter.core.bdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * <Description> 本地缓存数据，通过生产者消费者机制，增加服务访问的吞吐量<br>
 *
 * @param <T> the type parameter
 * @author 李志明<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月14日 <br>
 */
public class DataQueue<T extends Serializable> extends ICache {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataQueue.class);

    /**
     * 定义基于bdb的缓存队列
     */
    private BdbPersistentQueue<T> persistentQueue;

    /**
     * maxSize=0时表示无限制；
     */
    private int maxSize = 0;

    /**
     * 构造函数，初始化bdb
     *
     * @param t      泛函
     * @param dbDir  数据库所在位置
     * @param dbName 数据库名
     */
    public DataQueue(Class<T> t, String dbDir, String dbName) {
        // 判空
        if (StringUtils.isEmpty(dbDir)) {
            return;
        }
        File file = new File(dbDir);
        // 文件夹不存在
        if (!file.exists()) {
            // 创建文件夹
            file.mkdirs();
        }
        this.persistentQueue = new BdbPersistentQueue<>(dbDir, dbName, t);
    }

    /**
     * Description: Generate <br>
     *
     * @param data data
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public void generate(T data) {
        synchronized (this.persistentQueue) {
            if (this.maxSize != 0) {
                while (this.persistentQueue.size() >= this.maxSize && !isClosed()) {
                    try {
                        this.persistentQueue.wait();
                    } catch (InterruptedException e) {
                        LOGGER.error("wait queue failed", e);
                        Thread.currentThread().interrupt();
                    }
                }
            }

            this.persistentQueue.add(data);
            this.persistentQueue.notifyAll();
        }
    }

    /**
     * Description: Poll <br>
     *
     * @return t
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public T poll() {
        T result;
        synchronized (this.persistentQueue) {
            while (this.persistentQueue.isEmpty() && !isClosed()) {
                try {
                    this.persistentQueue.wait();
                } catch (InterruptedException e) {
                    LOGGER.error("wait queue failed", e);
                    Thread.currentThread().interrupt();
                }
            }
            result = this.persistentQueue.poll();

            this.persistentQueue.notifyAll();

            return result;
        }
    }

    /**
     * Description: 批量获取队列内容 <br>
     *
     * @return list
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public List<T> drain() {
        List<T> resultList = new LinkedList<>();
        synchronized (this.persistentQueue) {
            while (this.persistentQueue.isEmpty()) {
                try {
                    this.persistentQueue.wait();
                } catch (InterruptedException e) {
                    LOGGER.debug("wait persistent queue failed when drain", e);
                    Thread.currentThread().interrupt();
                }
            }

            while (!this.persistentQueue.isEmpty()) {
                try {
                    T tmp = this.persistentQueue.remove();
                    if (tmp != null) {
                        resultList.add(tmp);
                    }
                } catch (Exception localException) {
                    LOGGER.error("wait persistent queue failed when remove", localException);
                }
            }

            this.persistentQueue.notifyAll();

            return resultList;
        }
    }

    /**
     * Description: Close cache <br>
     *
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @Override
    public void closeCache() {
        synchronized (this.persistentQueue) {
            this.persistentQueue.notifyAll();
            this.setClosed(true);
        }
        this.persistentQueue.closeDb();
    }
}
