package com.tuanbaol.messageclient.websocket;

import com.sun.javafx.binding.StringFormatter;
import com.tuanbaol.messageclient.Logger;
import com.tuanbaol.messageclient.MessageNotifyFrame;
import com.tuanbaol.messageclient.constant.WebsocketStatusEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;

import static com.tuanbaol.messageclient.Logger.info;

/**
 * 基于websocket的netty客户端
 */
public class ClientByNetty {
    private final static String SERVER_URL_PATTERN = "ws://%s:%s/messageserver/ws";
    private MessageNotifyFrame notifyFrame;
    private Channel channel;
    private Integer status=WebsocketStatusEnum.DISCONNECTED.getCode();

    public ClientByNetty(MessageNotifyFrame notifyFrame) {
        this.notifyFrame = notifyFrame;
    }

    public void init() throws Exception {
        //netty基本操作，线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //netty基本操作，启动类
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("http-codec", new HttpClientCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024 * 10));
                        pipeline.addLast("hookedHandler", new WebSocketClientHandler(ClientByNetty.this, notifyFrame));
                    }
                });
        URI websocketURI = new URI(getServerUrl(notifyFrame.getHost(), notifyFrame.getPort()));
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String) null, true, httpHeaders);
        //客户端与服务端连接的通道，final修饰表示只会有一个
        channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
        WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();
        info("握手成功");

        //给服务端发送的内容，如果客户端与服务端连接成功后，可以多次掉用这个方法发送消息
        sendMessage(channel, "客户端连接成功。");
        sendPong(channel);
    }

    private String getServerUrl(String host, Integer port) {
        return StringFormatter.format(SERVER_URL_PATTERN, host, port).getValue();
    }

    public void sendMessage(Channel channel, String msg) {
        TextWebSocketFrame frame = new TextWebSocketFrame(msg);
        channel.writeAndFlush(frame).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("消息发送成功，发送的消息是：" + msg);
            } else {
                System.out.println("消息发送失败 " + channelFuture.cause().getMessage());
            }
        });
    }

    private void sendPong(Channel channel) {
        new Thread(() -> {
            while (WebsocketStatusEnum.CONNECTED.getCode().equals(status)) {
                sendMessage(channel, "1");
                synchronized (channel) {
                    try {
                        channel.wait(30000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void close() {
        if (channel == null) {
            return;
        }
        try {
            channel.write(new CloseWebSocketFrame());
            channel.close();
        } catch (Exception e) {
            System.out.println("关闭websocket连接失败。");
            e.printStackTrace();
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
