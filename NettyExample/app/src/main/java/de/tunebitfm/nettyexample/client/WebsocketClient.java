package de.tunebitfm.nettyexample.client;

import android.util.Log;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;

public class WebsocketClient {

    public static final String LOG_TAG = "websocket-client";

    private final String serviceAddress = "wss://echo.websocket.org/";

    @Getter(AccessLevel.PACKAGE) private WebsocketEndpointAddress address;

    public final EventLoopGroup eventLoop = new NioEventLoopGroup(1);

    /** The channel of the connection */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) private Channel channel = null;

    /** The Handshaker */
    @Getter(AccessLevel.PACKAGE) private WebSocketClientHandshaker handshaker;

    private Bootstrap bootstrap;

    public WebsocketClient() {
        try {
            this.address = new WebsocketEndpointAddress(serviceAddress);
        } catch(Throwable throwable) {
            Log.e(LOG_TAG, "Uups. An error occurred", throwable);
        }
    }

    public void connect() { // Android does not allow network on the UI thread
        this.eventLoop.execute(new Runnable() {

            @Override
            public void run() {
                doConnect();
            }

        });
    }

    private void doConnect() {
        try {

            if(this.bootstrap == null) {

                final SslContext sslContext;

                if(this.address.isEncrypted()) {
                    sslContext = SslContextBuilder.forClient().build();
                } else {
                    sslContext = null;
                }

                this.bootstrap = new Bootstrap();
                this.bootstrap.group(this.eventLoop)
                        .channel(NioSocketChannel.class)
                        .handler(new WebsocketClientInitializer(this, sslContext));
            }

            this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                    this.address.getUri(), WebSocketVersion.V13, null, false, new DefaultHttpHeaders()
            );

            Log.i(LOG_TAG, "Connect to " + this.address.getUri().toString());

            // Connect to websocket endpoint
            this.bootstrap.connect(this.address.getHost(), this.address.getPort()).addListener(new GenericFutureListener<ChannelFuture>() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if(!future.isSuccess()) {
                        Log.e(LOG_TAG, "An error occurred while trying to connect.", future.cause());
                        return;
                    }

                    channel = future.channel();
                }

            });

        } catch(Throwable throwable) {
            Log.e(LOG_TAG, "Uups. An error occurred while trying to connect.", throwable);
        }
    }

    public void sendMessage(String message) {
        this.channel.writeAndFlush(new TextWebSocketFrame(message));
    }

}
