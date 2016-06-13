//package com.me2me.io.server;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 上海拙心网络科技有限公司出品
// * Author: 赵朋扬
// * Date: 2016/6/13.
// */
//@Slf4j
//public class MeIOServer {
//
//    public static void main(String[] args) throws InterruptedException {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        ServerBootstrap b = new ServerBootstrap();
//        b.group(bossGroup,workerGroup)
//                .channel(NioServerSocketChannel.class)
//                .childHandler(new ChannelInitializer<SocketChannel>() {
//            @Override
//            protected void initChannel(SocketChannel ch) throws Exception {
//                ch.pipeline().addLast();
//            }
//        }).option(ChannelOption.SO_BACKLOG,1024).childOption(ChannelOption.SO_KEEPALIVE,true);
//
//        log.info("monitor at {}",8989);
//        ChannelFuture future = b.bind(8989).sync();
//        future.channel().closeFuture().sync();
//    }
//
//
//}
