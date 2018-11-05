package com.clawhub.registrycenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.registrycenter.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * <Description>注册中心网关<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/5 16:22 <br>
 */
@RestController
@RequestMapping("server")
public class ServerController {

    /**
     * The Server service.
     */
    @Autowired
    private ServerService serverService;

    /**
     * 获取所有的服务
     *
     * @return the string
     */
    @GetMapping("getAllServer")
    public String getAllServer() {
        Set<String> set = serverService.getAllServer();
        return JSONObject.toJSONString(set);
    }

    /**
     * 获取服务的提供者与消费者
     *
     * @param server 服务名
     * @return 服务的提供者与消费者
     */
    @GetMapping("getAllClient/{server}")
    public String getAllClient(@PathVariable("server") String server) {
        Set<String> set = serverService.getAllClient(server);
        return JSONObject.toJSONString(set);
    }
}
