package club.xubowei.rpc.serviceinfoclient.registry.remote;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xubowei on 18/02/2017.
 */
public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private static volatile boolean started = false;

    public static void start(int port) {
        if (!started) {
            synchronized (NettyServer.class) {
                if (!started) {
                    EventLoopGroup bossGroup = new NioEventLoopGroup();
                    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ShowServiceINfoServerHandler2());
                                }
                            }).option(ChannelOption.SO_BACKLOG, 128)
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    try {
                        ChannelFuture f = bootstrap.bind(port).sync();
                        f.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (future.isSuccess()) {
                                    started = true;
                                    log.info("NettyServer started!");
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        log.error("NettyServer started failed:"+e.getMessage(), e);
                    }
                }
            }
        }
    }
}
