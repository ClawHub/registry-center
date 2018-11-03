package com.clawhub.registrycenter.core.bdb;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.EnvironmentConfig;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <Description> 持久化队列 <br>
 * 持久化队列,基于BDB实现,也继承Queue,以及可以序列化.<br>
 * 但不等同于Queue的时,不再使用后需要关闭 相比一般的内存Queue,插入和获取值需要多消耗一定的时间 <br>
 * 这里为什么是继承AbstractQueue而不是实现Queue接口,是因为只要实现offer,peek,poll几个方法即可,<br>
 * 其他如remove,addAll,AbstractQueue会基于这几个方法去实现<br>
 *
 * @param <E> the type parameter
 * @author 李志明<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月14日 <br>
 */
public class BdbPersistentQueue<E extends Serializable> extends AbstractQueue<E> implements Serializable {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BdbPersistentQueue.class);

    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = -4175431971478491701L;

    /**
     * 数据库环境
     */
    private transient BdbEnvironment dbEnv;

    /**
     * 数据库，用于保存值，支持队列序列化
     */
    private transient Database queueDb;

    /**
     * 持久化Map,Key为指针位置,Value为值,无需序列化
     */
    private transient StoredSortedMap<Long, E> queueMap;

    /**
     * 数据库所在位置
     */
    private transient String dbDir;

    /**
     * 数据库名
     */
    private transient String dbName;

    /**
     * 头部指针
     */
    private AtomicLong headIndex;

    /**
     * 尾部指针
     */
    private AtomicLong tailIndex;

    /**
     * 当前获取的值
     */
    private transient E peekItem = null;

    /**
     * 构造函数,传入BDB数据库
     *
     * @param db the db
     * @param valueClass the value class
     * @param classCatalog the class catalog
     */
    public BdbPersistentQueue(Database db, Class<E> valueClass, StoredClassCatalog classCatalog) {
        // bdb数据库
        this.queueDb = db;
        // 数据库名称
        this.dbName = db.getDatabaseName();
        // 绑定数据库
        bindDatabase(queueDb, valueClass, classCatalog);
        // 初始化指针
        initIndex();

    }

    /**
     * 构造函数,传入BDB数据库位置和名字,自己创建数据库
     *
     * @param dbDir 数据库所在位置
     * @param dbName 数据库名
     * @param valueClass the value class
     */
    public BdbPersistentQueue(String dbDir, String dbName, Class<E> valueClass) {
        this.dbDir = dbDir;
        this.dbName = dbName;
        // 创建以及绑定数据库
        createAndBindDatabase(valueClass);
        // 初始化指针
        initIndex();

    }

    /**
     * Create and bind database.
     *
     * @param valueClass the value class
     */
    private void createAndBindDatabase(Class<E> valueClass) {
        // 创建数据库
        Database db = createDb();
        // 绑定数据库
        bindDatabase(db, valueClass, dbEnv.getClassCatalog());
    }

    /**
     * 创建数据库
     *
     * @return the database
     */
    private Database createDb() {
        // 数据库位置
        File envFile = new File(dbDir);
        // 数据库环境配置
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        dbEnv = new BdbEnvironment(envFile, envConfig);
        // 数据库配置
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(false);
        dbConfig.setDeferredWrite(true);
        return dbEnv.openDatabase(null, dbName, dbConfig);
    }

    /**
     * Bind database.
     *
     * @param db the db
     * @param valueClass the value class
     * @param classCatalog the class catalog
     */
    private void bindDatabase(Database db, Class<E> valueClass, StoredClassCatalog classCatalog) {
        EntryBinding<E> valueBinding = TupleBinding.getPrimitiveBinding(valueClass);
        if (valueBinding == null) {
            valueBinding = new SerialBinding<>(classCatalog, valueClass);
        }
        queueDb = db;
        queueMap = new StoredSortedMap<>(db, TupleBinding.getPrimitiveBinding(Long.class), valueBinding, true);
    }

    /**
     * Init index.
     */
    private void initIndex() {
        if (queueMap.isEmpty()) {
            headIndex = new AtomicLong(0);
            tailIndex = new AtomicLong(0);
        } else {
            headIndex = new AtomicLong(queueMap.firstKey());
            tailIndex = new AtomicLong(queueMap.lastKey() + 1);
        }
    }

    /**
     * Gets head index.
     *
     * @return the head index
     */
    public AtomicLong getHeadIndex() {
        return headIndex;
    }

    /**
     * Sets head index.
     *
     * @param headIndex the head index
     */
    public void setHeadIndex(AtomicLong headIndex) {
        this.headIndex = headIndex;
    }

    /**
     * Gets tail index.
     *
     * @return the tail index
     */
    public AtomicLong getTailIndex() {
        return tailIndex;
    }

    /**
     * Sets tail index.
     *
     * @param tailIndex the tail index
     */
    public void setTailIndex(AtomicLong tailIndex) {
        this.tailIndex = tailIndex;
    }

    /**
     * Iterator iterator.
     *
     * @return the iterator
     */
    @Override
    public Iterator<E> iterator() {
        return queueMap.values().iterator();
    }

    /**
     * Size int.
     *
     * @return the int
     */
    @Override
    public int size() {
        synchronized (tailIndex) {
            synchronized (headIndex) {
                return (int) (tailIndex.get() - headIndex.get());
            }
        }
    }

    /**
     * Offer boolean.
     *
     * @param e the e
     * @return the boolean
     */
    @Override
    public boolean offer(E e) {
        synchronized (tailIndex) {
            queueMap.put(tailIndex.getAndIncrement(), e);
        }
        return true;
    }

    /**
     * Peek e.
     *
     * @return the e
     */
    @Override
    public E peek() {
        synchronized (headIndex) {
            if (peekItem != null) {
                return peekItem;
            }
            E headItem = null;
            while (headItem == null && headIndex.get() < tailIndex.get()) {
                headItem = queueMap.get(headIndex.get());
                if (headItem != null) {
                    peekItem = headItem;
                    continue;
                }
                headIndex.incrementAndGet(); //
            }
            return headItem;
        }
    }

    /**
     * Poll e.
     *
     * @return the e
     */
    @Override
    public E poll() {
        synchronized (headIndex) {
            E headItem = peek();
            if (headItem != null) {
                queueMap.remove(headIndex.getAndIncrement());
                peekItem = null;
                return headItem;
            }
        }
        return null;
    }

    /**
     * Poll all list.
     *
     * @return the list
     */
    public List<E> pollAll() {
        List<E> result = new ArrayList<>();
        synchronized (headIndex) {
            E item;
            while (headIndex.get() < this.tailIndex.get()) {

                item = queueMap.remove(headIndex.getAndIncrement());

                if (item != null) {

                    result.add(item);
                }
            }

        }
        return result;

    }

    /**
     * 关闭所用的BDB数据库但不关闭数据库环境
     */
    private void close() {
        try {
            if (queueDb != null) {
                queueDb.sync();
                queueDb.close();
            }

        } catch (Exception e) {
            LOGGER.error("close BDB failed", e);
        }
    }

    /**
     * 关闭所用的BDB数据库 同时关闭数据库环境
     */
    public void closeDb() {
        try {

            close();
            if (dbEnv != null && queueDb != null) {
                dbEnv.close();
            }
        } catch (DatabaseNotFoundException e) {
            LOGGER.error("close BDB and base failed", e);
        }
    }

    /**
     * 清理,会清空数据库,并且删掉数据库所在目录,慎用.如果想保留数据,请调用close()
     */
    @Override
    public void clear() {
        try {
            close();
            if (dbEnv != null && queueDb != null) {
                if (dbName == null) {
                    dbName = queueDb.getDatabaseName();
                }
                dbEnv.removeDatabase(null, dbName);
                dbEnv.close();
            }
        } catch (Exception e) {
            LOGGER.error("clear BDB failed", e);
        } finally {
            try {
                if (this.dbDir != null) {
                    FileUtils.deleteDirectory(new File(this.dbDir));
                }

            } catch (IOException e) {
                LOGGER.error("del DBD direct failed", e);
            }
        }
    }

}
