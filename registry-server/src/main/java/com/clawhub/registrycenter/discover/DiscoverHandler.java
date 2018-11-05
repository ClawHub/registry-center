package com.clawhub.registrycenter.discover;

import com.clawhub.registrycenter.core.spring.SpringContextHelper;

/**
 * <Description>服务发现处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 16:24 <br>
 */
public class DiscoverHandler {

    /**
     * The constant discoverAdapter.
     */
    private static DiscoverAdapter discoverAdapter = (DiscoverAdapter) SpringContextHelper.getBean("discoverAdapter");

    /**
     * 服务发现
     *
     * @param server server
     * @return the string
     */
    public static String handle(String server) {
        return discoverAdapter.discover(server);
    }
}
