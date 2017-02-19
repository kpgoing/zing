package club.xubowei.rpc.serviceinfoclient.registry.remote;

import club.xubowei.rpc.serviceinfoclient.registry.AbstractRegistryServiceInfoLoaderImpl;
import club.xubowei.rpc.serviceinfoclient.registry.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by xubowei on 18/02/2017.
 */
public class ShowServiceInfoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            String order = String.valueOf(byteBuf.readBytes(Constants.SHOW_ALL_SEVICES.length()));
            if (order.equals(Constants.SHOW_ALL_SEVICES)) {
                ctx.writeAndFlush(AbstractRegistryServiceInfoLoaderImpl.getAllServicesInfo().toString());
            } else {
                ctx.writeAndFlush("error order!");
            }

        }finally {
            byteBuf.release();
        }
    }
}
