package com.clawhub.registrycenter.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <Description>TcpClientHandler<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 10:53 <br>
 */
public class TcpClientHandler extends ChannelHandlerAdapter {
    /**
     * The constant LOGGER.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(TcpClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] dst = new byte[buf.capacity()];
            buf.readBytes(dst);
            LOGGER.info("client接收到服务器返回的消息:" + new String(dst));
            ReferenceCountUtil.release(msg);
        } else {
            LOGGER.warn("error object");
        }

    }
}
