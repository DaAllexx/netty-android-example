package de.tunebitfm.nettyexample.client;

import android.util.Log;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebsocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private WebsocketClient client;

    public WebsocketClientHandler(WebsocketClient client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        Log.i(WebsocketClient.LOG_TAG, "Connected to the websocket service!");
        this.client.getHandshaker().handshake(context.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {

        Channel channel = context.channel();

        if (!this.client.getHandshaker().isHandshakeComplete()) {
            this.client.getHandshaker().finishHandshake(channel, (FullHttpResponse) message);
            return;
        }

        if (message instanceof FullHttpResponse) {
            Log.e(WebsocketClient.LOG_TAG, "Got unexpected FullHttpResponse");
            return;
        }

        WebSocketFrame frame = (WebSocketFrame) message;

        if (frame instanceof TextWebSocketFrame) {

            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            Log.i(WebsocketClient.LOG_TAG, "Received message: " + textFrame.text());

        } else if (frame instanceof PingWebSocketFrame) {
            channel.writeAndFlush(new PongWebSocketFrame());
        } else if (frame instanceof CloseWebSocketFrame) {
            channel.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) {
        Log.e(WebsocketClient.LOG_TAG, "Uups. An error occurred.", throwable);
        context.close();
    }

}
