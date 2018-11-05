package com.clawhub.registrycenter.heartbeat;

import com.clawhub.registrycenter.core.ClientBean;
import com.clawhub.registrycenter.core.ClientPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <Description>心跳适配<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/5 13:54 <br>
 */
@Component
public class HeartbeatAdapter {

    /**
     * The Client pool.
     */
    @Autowired
    private ClientPool clientPool;

    public void heartBeat(ClientBean info) {
        clientPool.heartBeat(info);
    }
}