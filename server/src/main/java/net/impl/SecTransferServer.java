package net.impl;

import config.Configure;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import net.ISecTranferServer;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

@Slf4j
@Service
public class SecTransferServer implements ISecTranferServer {

    public static AttributeKey<String> CypherKey = AttributeKey.valueOf("Cypher");

    public void serve(Configure configure) {
        // 用来接收进来的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理已经被接收的连接，一旦bossGroup接收到连接，就会把连接信息注册到workerGroup上
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // nio服务的启动类
            ServerBootstrap sbs = new ServerBootstrap();
            // 配置nio服务参数
            sbs.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class) // 说明一个新的Channel如何接收进来的连接
                    .option(ChannelOption.SO_BACKLOG, 128) // tcp最大缓存链接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //保持连接
                    .handler(new LoggingHandler(LogLevel.INFO)) // 打印日志级别
                    .childHandler(new ChannelInitializer<EpollServerSocketChannel>() {
                        @Override
                        protected void initChannel(EpollServerSocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                    0, 2, 0, 2)).
                                    addLast();

                        }
                    })
                    .childAttr(CypherKey,configure.getCypher());

            System.err.println("server 开启--------------");
            // 绑定端口，开始接受链接
            for (int port : configure.getPortPool()) {
                sbs.bind(port);
            }
        } catch (Exception e) {
            log.error("Exception when port bind!", e);
        }
    }

}
