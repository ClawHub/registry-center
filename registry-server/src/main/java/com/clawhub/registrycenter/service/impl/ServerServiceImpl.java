package com.clawhub.registrycenter.service.impl;

import com.clawhub.registrycenter.client.ClientPool;
import com.clawhub.registrycenter.constant.ParamConstant;
import com.clawhub.registrycenter.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * <Description>服务逻辑<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/5 16:28 <br>
 */
@Service
public class ServerServiceImpl implements ServerService {

    /**
     * The Client pool.
     */
    @Autowired
    private ClientPool clientPool;

    @Override
    public Set<String> getAllServer() {
        Set<String> allServer = new HashSet<>();
        Set<String> allKey = clientPool.getAllKey();
        for (String key : allKey) {
            String[] arr = key.split(ParamConstant.UNDER_LINE);
            allServer.add(arr[1]);
        }
        return allServer;
    }

    @Override
    public Set<String> getAllClient(String server) {
        Set<String> allClient = new HashSet<>();
        Set<String> allKey = clientPool.getAllKey();
        for (String key : allKey) {
            String[] arr = key.split(ParamConstant.UNDER_LINE);
            if (server.equals(arr[1])) {
                allClient.add(clientPool.get(key));
            }

        }
        return allClient;
    }
}
