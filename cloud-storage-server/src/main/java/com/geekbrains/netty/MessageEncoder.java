package com.geekbrains.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class MessageEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String message = (String) msg;
        log.debug("received: {}", msg);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(message.getBytes(StandardCharsets.UTF_8));
        buffer.retain();
        ctx.writeAndFlush(buffer);
    }
}
