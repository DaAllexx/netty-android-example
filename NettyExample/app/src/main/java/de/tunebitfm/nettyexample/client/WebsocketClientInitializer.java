package de.tunebitfm.nettyexample.client;

import javax.net.ssl.SSLEngine;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

public class WebsocketClientInitializer extends ChannelInitializer<SocketChannel> {

    private WebsocketClient client;

    private SslContext sslContext = null;

    public WebsocketClientInitializer(WebsocketClient client, SslContext sslContext) {
        this.client = client;
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();

        if(this.sslContext != null) {
            pipeline.addLast(this.createSslHandler(sslContext, channel));
        }

        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebsocketClientHandler(this.client));
    }

    private SslHandler createSslHandler(SslContext context, Channel channel) {
        try {

            SSLEngine engine = context.newEngine(
                    channel.alloc(), this.client.getAddress().getHost(), this.client.getAddress().getPort()
            );

            engine.setUseClientMode(true);
            engine.setEnabledProtocols(new String[] { "TLSv1.2", "TLSv1.1", "TLSv1" });

            return new SslHandler(engine);

        } catch (Exception e) {
            return null;
        }
    }
}
