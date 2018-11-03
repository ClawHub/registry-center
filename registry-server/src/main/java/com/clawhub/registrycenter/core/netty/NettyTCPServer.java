package com.clawhub.registrycenter.core.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <Description>基于Netty的TCP短链接服务端<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 10:27 <br>
 */
@Component
public class NettyTCPServer {
    /**
     * 日志记录器
     */
    private Logger logger = LoggerFactory.getLogger(NettyTCPServer.class);

    /**
     * 用于TCP绑定的IP
     */
    @Value("${netty.tcp.server.ip}")
    private String ip;
    /**
     * 用于TCP绑定的端口
     */
    @Value("${netty.tcp.server.port}")
    private int port;
    /**
     * 用于分配处理业务线程的线程组个数
     */
    private int bossGroupSize = Runtime.getRuntime().availableProcessors() * 2;    //默认
    /**
     * 业务出现线程大小
     */
    private int workerGroupSize = 1000;
    /**
     * Boss线程：由这个线程池提供的线程是boss种类的，用于创建、连接、绑定socket， （有点像门卫）然后把这些socket传给worker线程池。
     * 在服务器端每个监听的socket都有一个boss线程来处理。在客户端，只有一个boss线程来处理所有的socket。
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupSize);
    /**
     * Worker线程：Worker线程执行所有的异步I/O，即处理操作
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup(workerGroupSize);

    /**
     * Run.
     */
    @PostConstruct
    public void run() {
        logger.info("开始启动TCP服务器...");
        // ServerBootstrap 启动NIO服务的辅助启动类,负责初始话netty服务器，并且开始监听端口的socket请求
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        // 设置非阻塞,用它来建立新accept的连接,用于构造serversocketchannel的工厂类
        b.channel(NioServerSocketChannel.class);
        //对出入的数据进行的业务操作
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                //通用TCP黏包解决方案
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                //业务处理
                pipeline.addLast(workerGroup, new TcpServerHandler());
            }
        });
//	b.childOption(ChannelOption.SO_KEEPALIVE,true);
        b.option(ChannelOption.SO_BACKLOG, 10000);
        try {
            b.bind(ip, port).sync();
        } catch (InterruptedException e) {
            logger.error("启动TCP服务器失败！", e);
        }
        logger.info("TCP服务器已启动");
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}