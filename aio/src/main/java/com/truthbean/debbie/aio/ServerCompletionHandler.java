package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.server.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-20 16:10
 */
public class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private final AsynchronousServerSocketChannel listener;
    private final BeanFactoryHandler beanFactoryHandler;
    private final AioServerConfiguration configuration;

    public ServerCompletionHandler(AioServerConfiguration configuration, SessionManager sessionManager,
                                   final BeanFactoryHandler beanFactoryHandler,
                                   final AsynchronousServerSocketChannel listener) {
        this.listener = listener;
        this.beanFactoryHandler = beanFactoryHandler;
        this.configuration = configuration;
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel attachment) {
        /*if (attachment != null && !attachment.isOpen()) {
            // accept the next connection
            listener.accept(attachment, this);
        } else {
            listener.accept(null, this);
        }*/

        // handle this connection
        RouterRequest routerRequest = handleRequest(channel);
        handleResponse(routerRequest, channel, attachment);
    }

    private RouterRequest handleRequest(AsynchronousSocketChannel channel) {
        try {
            LOGGER.debug("remote address: " + channel.getRemoteAddress().toString());
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        //获取客户端发送的请求
        var requestCompleteHandler = new RequestCompleteHandler();
        return requestCompleteHandler.handle(channel);
    }

    private void handleResponse(RouterRequest routerRequest, AsynchronousSocketChannel channel,
                                             AsynchronousServerSocketChannel attachment) {
        if (routerRequest != null) {
            var responseCompletionHandler = new ResponseCompletionHandler(beanFactoryHandler, routerRequest, configuration);
            responseCompletionHandler.handle(channel);
        }

        try {
            channel.shutdownOutput();
            channel.shutdownInput();
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        try {
            channel.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        if (attachment != null)
            attachment.accept(attachment, this);
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
        LOGGER.error("", exc);
        try {
            attachment.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(ServerCompletionHandler.class);
}
