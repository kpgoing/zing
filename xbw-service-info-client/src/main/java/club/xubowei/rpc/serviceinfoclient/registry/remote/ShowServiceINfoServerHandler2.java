package club.xubowei.rpc.serviceinfoclient.registry.remote;

import club.xubowei.rpc.serviceinfoclient.registry.AbstractRegistryServiceInfoLoaderImpl;
import club.xubowei.rpc.serviceinfoclient.registry.constant.Constants;
import com.google.common.base.Strings;
import com.google.common.primitives.Bytes;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Created by xubowei on 19/02/2017.
 */
public class ShowServiceINfoServerHandler2 extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("%%%%%%%%%%%%%%%%");
        System.out.println("msg = " + msg);
        String response;
        boolean close = false;
        if (msg.isEmpty()) {
            response = "use showallserviceinfos\r\n";
        } else if (msg.toLowerCase().equals(Constants.SHOW_ALL_SEVICES + "\r\n")) {
            response = AbstractRegistryServiceInfoLoaderImpl.getAllServicesInfo().toString() + "\r\n";
        } else if (msg.toLowerCase().equals("bye")) {
            response = "byebye\r\n";
            close = true;
        } else {
            response = "try the correct order!\r\n";
        }

        ChannelFuture future = ctx.writeAndFlush(response);

        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
