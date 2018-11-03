package com.clawhub.registrycenter.core.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <Description>TCP短链接 客户端<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 10:48 <br>
 */
@Component
public class NettyTCPClient {
    /**
     * 日志记录器
     */
    private Logger logger = LoggerFactory.getLogger(NettyTCPClient.class);

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

    private Bootstrap bootstrap;

    /**
     * Init.
     */
    public void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("handler", new TcpClientHandler());
            }
        });
//      bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }


    /**
     * Gets channel.
     *
     * @return the channel
     */
    private Channel getChannel() {
        Channel channel = null;
        try {
            channel = bootstrap.connect(ip, port).sync().channel();
        } catch (Exception e) {
            logger.error(String.format("连接Server(IP[%s],PORT[%s])失败", ip, port), e);
        }
        return channel;
    }

    /**
     * Send msg.
     *
     * @param msg the msg
     * @throws Exception the exception
     */
    public void sendMsg(String msg) throws Exception {
        Channel channel = getChannel();
        if (channel == null) {
            logger.warn("消息发送失败,连接尚未建立!");
            return;
        }
        byte[] value = msg.getBytes("UTF-8");
        ByteBufAllocator alloc = channel.alloc();
        ByteBuf buf = alloc.buffer(value.length);
        buf.writeBytes(value);
        channel.writeAndFlush(buf).sync();
    }

}