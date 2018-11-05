package com.clawhub.registrycenter.service;

import com.clawhub.registrycenter.client.ClientBean;

import java.util.Set;

/**
 * <Description>服务接口<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/5 16:26 <br>
 */
public interface ServerService {

    /**
     * 获取所有的服务
     *
     * @return 所有的服务
     */
    Set<String> getAllServer();

    /**
     * 获取所有客户端
     *
     * @param server 服务名
     * @return 所有所有客户端
     */
    Set<String> getAllClient(String server);

    /**
     * 改变客户端状态
     *
     * @param clientBean 客户端信息
     */
    void changeActive(ClientBean clientBean);
}
