package com.tuanbaol.messageclient.websocket;

import com.tuanbaol.messageclient.MessageNotifyFrame;
import com.tuanbaol.messageclient.bean.Message;
import com.tuanbaol.messageclient.constant.WebsocketStatusEnum;
import com.tuanbaol.messageclient.util.*;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;

import static com.tuanbaol.messageclient.Logger.*;
import static com.tuanbaol.messageclient.util.LogUtil.addTimeFormat;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private ClientByNetty clientByNetty;
    //握手的状态信息
    WebSocketClientHandshaker handshaker;
    //netty自带的异步处理
    ChannelPromise handshakeFuture;
    private MessageNotifyFrame nofifyFrame;
    private String rn = "\r\n";
    private final static String ENCRYPT_PREFIX = "Pp/eEiT[";

    public WebSocketClientHandler(ClientByNetty clientByNetty, MessageNotifyFrame nofifyFrame) {
        this.clientByNetty = clientByNetty;
        this.nofifyFrame = nofifyFrame;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            info("当前握手的状态" + this.handshaker.isHandshakeComplete());
            Channel ch = ctx.channel();
            //进行握手操作
            if (!this.handshaker.isHandshakeComplete()) {
                configFinishHandshake((FullHttpResponse) msg, ch);
            } else if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                info("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ")");
            } else {
                dealRecivedMessage((WebSocketFrame) msg, ch);
            }
        } catch (Exception e) {

            info("error occurred when handle channel read info.");
            e.printStackTrace();
        }
    }

    private void configFinishHandshake(FullHttpResponse msg, Channel ch) {
        FullHttpResponse response;
        try {
            response = msg;
            //握手协议返回，设置结束握手
            this.handshaker.finishHandshake(ch, response);
            //设置成功
            this.handshakeFuture.setSuccess();
            info("服务端的消息" + response.headers());
        } catch (WebSocketHandshakeException var7) {
            FullHttpResponse res = msg;
            String errorMsg = String.format("握手失败,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
            this.handshakeFuture.setFailure(new Exception(errorMsg));
        }
    }

    private void dealRecivedMessage(WebSocketFrame msg, Channel ch) {
        //接收服务端的消息
        WebSocketFrame frame = msg;
        //文本信息
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            String decryptedMsg = checkAndDecryptText(textFrame.text());
            sendToNofifyFrame(decryptedMsg);
        }
        //二进制信息
        if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame) frame;
            info("BinaryWebSocketFrame");
        }
        //ping信息
        if (frame instanceof PongWebSocketFrame) {
            info("WebSocket Client received pong");
        }
        //关闭消息
        if (frame instanceof CloseWebSocketFrame) {
            info("receive close frame");
            ch.close();
        }
    }

    private String checkAndDecryptText(String text) {
        if (StringUtils.startsWith(text, ENCRYPT_PREFIX)) {
            info("客户端接收到加密消息。");
            String encrypted = text.substring(ENCRYPT_PREFIX.length());
            String msg = Base64GarbleUtil.decode(encrypted);
            info("解密后消息：" + msg);
            return msg;
        } else {
            info("客户端接收到消息:" + text);
            return text;
        }
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.startsWith("Pp/eEiT[=yJ[0aXRsZSI6Ium", ENCRYPT_PREFIX));
    }

    private void sendToNofifyFrame(String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }
        nofifyFrame.obtainMessage(JsonUtil.toObject(text, Message.class));
    }

    /**
     * Handler活跃状态，表示连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clientByNetty.setStatus(WebsocketStatusEnum.CONNECTED.getCode());
        callbackToFrame(WebsocketStatusEnum.CONNECTED);
        info("与服务端连接成功");
    }

    /**
     * 非活跃状态，没有连接远程主机的时候。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientByNetty.setStatus(WebsocketStatusEnum.DISCONNECTED.getCode());
        callbackToFrame(WebsocketStatusEnum.DISCONNECTED);
        warn("主机关闭");
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        clientByNetty.setStatus(WebsocketStatusEnum.DISCONNECTED.getCode());
        callbackToFrame(WebsocketStatusEnum.DISCONNECTED);
        String connInfo = addTimeFormat("连接异常：{}", cause.getMessage());
        sendMsgToFrame(connInfo);
        error("连接异常：{}", cause.getMessage());
        ctx.close();
    }

    private void callbackToFrame(WebsocketStatusEnum connInfo) {
        nofifyFrame.onConnChanged(connInfo);
    }

    private void sendMsgToFrame(String connInfo) {
        nofifyFrame.addMessageln(connInfo);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }
}
