package com.clawhub.registrycenter.client;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.core.lmdb.LmdbTemplate;
import com.clawhub.registrycenter.util.RegisterKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>客户池<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/5 14:08 <br>
 */
@Component
public class ClientPool {
    /**
     * The Lmdb template.
     */
    @Autowired
    private LmdbTemplate lmdbTemplate;

    /**
     * The Poll.
     */
    private Map<String, Long> poll = new ConcurrentHashMap<>();
    /**
     * 死亡时间间隔
     */
    @Value("${heart.beat.death.time.interval}")
    private long deathTimeInterval;

    /**
     * 注册客户端
     *
     * @param key   the key
     * @param value the value
     */
    public void register(String key, String value) {
        poll.put(key, System.currentTimeMillis());
        lmdbTemplate.put(key, value);
    }

    /**
     * 心跳
     *
     * @param info the info
     */
    public void heartBeat(ClientBean info) {
        String key = RegisterKeyUtil.getKey(info);
        Long lastTime = poll.get(key);
        Long now = System.currentTimeMillis();
        if (now - lastTime > deathTimeInterval) {
            //已死亡
            poll.remove(key);
            lmdbTemplate.delete(key);
        } else {
            poll.put(key, now);
        }
    }

    /**
     * 服务发现
     *
     * @param server the service
     * @return the list
     */
    public List<ClientBean> discover(String server) {
        List<ClientBean> serverList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : poll.entrySet()) {
            String key = entry.getKey();
            String value;
            if (key.startsWith("provider_" + server)) {
                //lmdb获取服务信息
                value = lmdbTemplate.getTxnRead(key);
                serverList.add(JSONObject.parseObject(value, ClientBean.class));
            }
        }
        return serverList;
    }

    /**
     * Get string.
     *
     * @param key the key
     * @return the string
     */
    public String get(String key) {
        return lmdbTemplate.getTxnRead(key);
    }

    /**
     * Put.
     *
     * @param key the key
     * @param val the val
     */
    public void put(String key, String val) {
        lmdbTemplate.put(key, val);
    }

    /**
     * Gets all key.
     *
     * @return the all key
     */
    public Set<String> getAllKey() {
        return poll.keySet();
    }
}
