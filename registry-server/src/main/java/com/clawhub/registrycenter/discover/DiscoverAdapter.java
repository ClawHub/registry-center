package com.clawhub.registrycenter.discover;

import com.clawhub.registrycenter.client.ClientPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * The Client pool.
     */
    @Autowired
    private ClientPool clientPool;

    /**
     * 服务发现
     *
     * @param server the service
     * @return the string
     */
    public String discover(String server) {
        //获取所有服务提供者
        List<String> providerInfos = clientPool.discover(server);

        //负载均衡

        //返回一个可用的服务提供者
        return providerInfos.get(0);
    }

}
