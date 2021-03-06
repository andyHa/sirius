/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.web.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Handler for low-level events in the HTTP pipeline.
 * <p>
 * Performs statistical tasks and performs basic filtering based on firewall rules.
 * </p>
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2013/09
 */
@ChannelHandler.Sharable
class LowLevelHandler extends ChannelDuplexHandler {
    static LowLevelHandler INSTANCE = new LowLevelHandler();

    @Override
    public void connect(ChannelHandlerContext ctx,
                        SocketAddress remoteAddress,
                        SocketAddress localAddress,
                        ChannelPromise future) throws Exception {
        WebServer.connections++;
        if (WebServer.connections < 0) {
            WebServer.connections = 0;
        }
        IPRange.RangeSet filter = WebServer.getIPFilter();
        if (!filter.isEmpty()) {
            if (!filter.accepts(((InetSocketAddress) remoteAddress).getAddress())) {
                WebServer.blocks++;
                if (WebServer.blocks < 0) {
                    WebServer.blocks = 0;
                }
                ctx.channel().close();
                future.setSuccess();
                return;
            }
        }
        super.connect(ctx, remoteAddress, localAddress, future);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            WebServer.bytesIn += ((ByteBuf) msg).readableBytes();
            if (WebServer.bytesIn < 0) {
                WebServer.bytesIn = 0;
            }
            WebServer.messagesIn++;
            if (WebServer.messagesIn < 0) {
                WebServer.messagesIn = 0;
            }
            ctx.pipeline().get(WebServerHandler.class).inbound(((ByteBuf) msg).readableBytes());
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            WebServer.bytesOut += ((ByteBuf) msg).writableBytes();
            if (WebServer.bytesOut < 0) {
                WebServer.bytesOut = 0;
            }
            WebServer.messagesOut++;
            if (WebServer.messagesOut < 0) {
                WebServer.messagesOut = 0;
            }
            ctx.pipeline().get(WebServerHandler.class).outbound(((ByteBuf) msg).readableBytes());
        }
        super.write(ctx, msg, promise);
    }


}
