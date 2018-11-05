package com.clawhub.registrycenter.core.lmdb;


import com.clawhub.registrycenter.constant.ParamConstant;
import org.lmdbjava.Cursor;
import org.lmdbjava.CursorIterator;
import org.lmdbjava.Dbi;
import org.lmdbjava.DbiFlags;
import org.lmdbjava.Env;
import org.lmdbjava.EnvFlags;
import org.lmdbjava.GetOp;
import org.lmdbjava.Stat;
import org.lmdbjava.Txn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lmdbjava.Env.create;

/**
 * <Description>Lmdb工具类 <br>
 * 程序启动可建立多个lmdb环境
 * 每个环境又可创建多个DB
 * 此例子只建立一个环境与DB
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @date 2018 -07-20 <br>
 */
@Component
public class LmdbTemplate {

    /**
     * The Env.
     */
    private Env<ByteBuffer> env;

    /**
     * The Db.
     */
    private Dbi<ByteBuffer> db;

    /**
     * The Path.
     */
    @Value("${lmdb.file.path}")
    private String path;

    /**
     * The Db name.
     */
    @Value("${lmdb.db.name}")
    private String dbName;

    /**
     * The Map size.
     */
    @Value("${lmdb.map.size}")
    private Long mapSize;

    /**
     * 项目启动时初始化LMDB环境与新建DB
     */
    @PostConstruct
    public void init() {
        env = create()
                .setMapSize(mapSize * ParamConstant.ONE_THOUSAND_AND_TWENTY_FOUR * ParamConstant.ONE_THOUSAND_AND_TWENTY_FOUR)
                .setMaxReaders(1)
                .setMaxDbs(1)
                .open(new File(path), EnvFlags.MDB_FIXEDMAP, EnvFlags.MDB_WRITEMAP);
        db = env.openDbi(dbName, DbiFlags.MDB_CREATE);
    }

    /**
     * LMDB中是否含有 key
     * 为只读事务
     *
     * @param key the key
     * @return the boolean
     */
    public Boolean containsKey(String key) {
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            try (Cursor<ByteBuffer> cursor = db.openCursor(txn)) {
                return cursor.get(stringToByteBuffer(key), GetOp.MDB_SET);
            }
        }
    }

    /**
     * Gets txn read.
     *
     * @param key the key
     * @return the txn read
     */
    public String getTxnRead(String key) {
        try (Txn<ByteBuffer> txn = env.txnRead()) {
            ByteBuffer found = db.get(txn, stringToByteBuffer(key));
            return byteBufferToString(found);
        }
    }

    /**
     * Gets txn write.
     *
     * @param key the key
     * @return the txn write
     */
    public String getTxnWrite(String key) {
        try (Txn<ByteBuffer> txn = env.txnWrite()) {
            ByteBuffer found = db.get(txn, stringToByteBuffer(key));
            return byteBufferToString(found);
        }
    }

    /**
     * Put.
     *
     * @param key the key
     * @param val the val
     */
    public void put(String key, String val) {
        db.put(stringToByteBuffer(key), stringToByteBuffer(val));
    }

    /**
     * Delete boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean delete(String key) {
        return db.delete(stringToByteBuffer(key));
    }

    /**
     * 查看当前LMDB的状态
     * 当前为可读可写事务，防止持续游标写入时，不可以查看状态
     *
     * @return the stat
     */
    public Stat state() {
        try (Txn<ByteBuffer> txn = env.txnWrite()) {
            return db.stat(txn);
        }

    }

    /**
     * Gets iterate.
     *
     * @return the iterate
     */
    public CursorIterator<ByteBuffer> getIterate() {
        return db.iterate(env.txnWrite());
    }

    /**
     * 设置为禁用可选检查,必要的检查还是会有的
     */
    public void noCheck() {
        System.setProperty(Env.DISABLE_CHECKS_PROP, Boolean.TRUE.toString());
    }

    /**
     * 拿到读写事务
     *
     * @return the txn write
     */
    public Txn<ByteBuffer> getTxnWrite() {
        return env.txnWrite();
    }

    /**
     * 根据事务打开游标
     *
     * @param txn the txn
     * @return the cursor
     */
    public Cursor<ByteBuffer> getCursor(Txn<ByteBuffer> txn) {
        return db.openCursor(txn);
    }

    /**
     * 将数据插入游标
     *
     * @param cursor the cursor
     * @param key    the key
     * @param val    the val
     */
    public void putByCursor(Cursor<ByteBuffer> cursor, String key, String val) {
        cursor.put(stringToByteBuffer(key), stringToByteBuffer(val));
    }

    /**
     * String 转 ByteBuffer.
     *
     * @param value the value
     * @return the byte buffer
     */
    public ByteBuffer stringToByteBuffer(String value) {
        final byte[] keyBytes = value.getBytes(UTF_8);
        final ByteBuffer keyBuffer = allocateDirect(keyBytes.length);
        keyBuffer.put(keyBytes).flip();
        return keyBuffer;
    }

    /**
     * Byte buffer to string string.
     *
     * @param buffer the buffer
     * @return the string
     */
    public String byteBufferToString(ByteBuffer buffer) {
        CharBuffer charBuffer;
        try {
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            buffer.flip();
            return charBuffer.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取环境
     *
     * @return the env
     */
    public Env<ByteBuffer> getEnv() {
        return env;
    }

    /**
     * 获取DB
     *
     * @return the db
     */
    public Dbi<ByteBuffer> getDb() {
        return db;
    }

}
