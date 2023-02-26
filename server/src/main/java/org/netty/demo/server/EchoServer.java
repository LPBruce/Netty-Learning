package org.netty.demo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.netty.demo.server.handler.EchoServerHandler;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println(
                    "Usage: " + EchoServer.class.getSimpleName() +
                            " <port>");
        }

        // 设置端口并开启服务
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        // 自定义channel事件处理器，实现了入站流程事件处理
        final EchoServerHandler serverHandler = new EchoServerHandler();

        // Channel 分配一个 EventLoop，用以处理所有事件
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建ServerBootstrap实例来引导和绑定服务器
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)  // 指定所使用的 NIO 传输 Channel
                    .localAddress(new InetSocketAddress(port))  // 使用指定的端口设置套接字地址
                    .childHandler(new ChannelInitializer<SocketChannel>() {  // channel的初始化
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            // 添加一个EchoServerHandler 到子Channel的 ChannelPipeline
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
