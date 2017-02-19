package club.xubowei.rpc.serviceinfoclient.registry.remote;

import club.xubowei.rpc.serviceinfoclient.registry.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by xubowei on 18/02/2017.
 */
public class ByteToOrderDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < Constants.SHOW_ALL_SEVICES.length()) {
            ctx.writeAndFlush("error order.");
            return;
        }

        out.add(String.valueOf(in.readBytes(Constants.SHOW_ALL_SEVICES.length())));
    }
}
