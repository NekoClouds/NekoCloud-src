package me.nekocloud.core.api.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.callback.Callback;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HttpInitializer extends ChannelInitializer<Channel> {

    Callback<String> callback;
    String host;

    boolean ssl;

    int port;

    @Override
    protected void initChannel(final Channel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("timeout", new ReadTimeoutHandler(
                HttpClient.TIMEOUT, TimeUnit.MILLISECONDS));

        if (ssl) {
            final SSLEngine engine = SslContextBuilder.forClient().build().newEngine(channel.alloc(), host, port);

            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline.addLast("http", new HttpClientCodec());
        pipeline.addLast("handler", new HttpHandler(callback));
    }
}
