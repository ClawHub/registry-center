package com.clawhub.registrycenter.heartbeat;

import com.clawhub.registrycenter.core.ClientBean;
import com.clawhub.registrycenter.core.spring.SpringContextHelper;

import java.util.List;

/**
 * <Description>心跳处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/5 13:41 <br>
 */
public class HeartbeatHandler {
    /**
     * The constant heartbeatAdapter.
     */
    private static HeartbeatAdapter heartbeatAdapter = (HeartbeatAdapter) SpringContextHelper.getBean("heartbeatAdapter");

    /**
     * 心跳处理
     *
     * @param infos infos
     * @return the string
     */
    public static String handle(List<ClientBean> infos) {
        for (ClientBean info : infos) {
            heartbeatAdapter.heartBeat(info);
        }
        return "success";
    }
}