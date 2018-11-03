package com.clawhub.registrycenter.core.netty;

import com.clawhub.registrycenter.core.MsgDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * <Description>业务处理<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018/11/3 10:28 <br>
 */
public class TcpServerHandler extends ChannelHandlerAdapter {
    /**
     * The constant logger.
     */
    private Logger logger = LoggerFactory.getLogger(TcpServerHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] dst = new byte[buf.capacity()];
            buf.readBytes(dst);
            //客户端消息中转
            String response = MsgDispatcher.process(new String(dst));
            //服务端返回消息
            byte[] dest = new byte[0];
            try {
                dest = (response).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ByteBuf destBuf = ctx.alloc().buffer(dest.length);
            destBuf.writeBytes(dest);
            ctx.channel().writeAndFlush(destBuf).addListener(ChannelFutureListener.CLOSE);

            ReferenceCountUtil.release(msg);
        } else {
            logger.warn("error object !");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}