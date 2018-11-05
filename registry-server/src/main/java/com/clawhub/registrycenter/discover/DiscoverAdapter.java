package com.clawhub.registrycenter.discover;

import com.clawhub.registrycenter.core.lmdb.LmdbTemplate;
import org.lmdbjava.Cursor;
import org.lmdbjava.CursorIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description>服务发现核心<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 16:27 <br>
 */
@Component
public class DiscoverAdapter {

    @Autowired
    private LmdbTemplate lmdbTemplate;

    /**
     * 服务发现
     *
     * @param server the server
     * @return the string
     */
    public String discover(String server) {
        //获取所有服务提供者
        List<String> providerInfos = new ArrayList<>();
        CursorIterator<ByteBuffer> iterator = lmdbTemplate.getIterate();
        while (iterator.hasNext()) {
            CursorIterator.KeyVal<ByteBuffer> keyVal = iterator.next();
            String key = lmdbTemplate.byteBufferToString(keyVal.key());
            if (key.startsWith("provider_" + server)) {
                providerInfos.add(lmdbTemplate.byteBufferToString(keyVal.val()));
            }
        }
        //负载均衡

        //返回一个可用的服务提供者
        return providerInfos.get(0);
    }

}