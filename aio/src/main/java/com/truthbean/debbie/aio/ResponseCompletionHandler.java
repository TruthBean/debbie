package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.router.MvcRouterHandler;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.util.OsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:19
 */
public class ResponseCompletionHandler {

    private final BeanFactoryHandler beanFactoryHandler;
    private final AioServerConfiguration configuration;
    private final RouterRequest routerRequest;

    public ResponseCompletionHandler(BeanFactoryHandler beanFactoryHandler, RouterRequest routerRequest,
                                     AioServerConfiguration configuration) {
        this.beanFactoryHandler = beanFactoryHandler;
        this.configuration = configuration;
        this.routerRequest = routerRequest;
    }

    private void writeChannel(AsynchronousSocketChannel channel, MediaTypeInfo responseType, Object result) {
        logger.trace("response content: " + result);
        var lf = OsUtils.getLf();
        // 响应头的参数
        var serverLine = ("Server: " + configuration.getServerMessage() + lf);
        var statusLine = "HTTP/" + configuration.getHttpVersion() + " 200 OK" + lf;
        var contentTypeLine = "Content-type: " + responseType.toString() + lf;

        var contentLength = 1024;
        if (result instanceof String) {
            contentLength = ((String) result).length();
        } else if (result instanceof byte[]) {
            contentLength = ((byte[]) result).length;
        }
        var contentLengthLine = "Content-Length: " + contentLength + lf;

        var resultStr = new StringBuffer(statusLine)
                .append(serverLine)
                .append(contentTypeLine)
                .append(contentLengthLine)
                .append(lf)
                .append(result)
                .append(lf).toString();

        logger.trace("response: " + resultStr);

        Future<Integer> future;
        if (result instanceof byte[]) {
            //先把头部转换成byte[]
            var header = new StringBuffer(statusLine)
                    .append(serverLine)
                    .append(contentTypeLine)
                    .append(contentLengthLine)
                    .append(lf).toString();

            var headerByteArray = header.toCharArray();
            //然后合并
            var merge = new byte[headerByteArray.length + ((byte[]) result).length];
            System.arraycopy(headerByteArray, 0, merge, 0, headerByteArray.length);
            System.arraycopy((byte[])result, 0, merge, headerByteArray.length, ((byte[]) result).length);
            future = channel.write(ByteBuffer.wrap(merge));
        } else {
            future = channel.write(ByteBuffer.wrap(resultStr.getBytes()));
        }

        try {
            Integer integer = future.get();
            logger.trace("future get: " + integer);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("", e);
        }

    }

    void handle(AsynchronousSocketChannel channel) {
        logger.trace("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        logger.trace(routerRequest.toString());
        logger.trace("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        logger.debug("--> handle response");
        RouterInfo matchedRouter = MvcRouterHandler.getMatchedRouter(routerRequest, configuration);
        RouterResponse routerResponse = MvcRouterHandler.handleRouter(matchedRouter, this.beanFactoryHandler);
        MediaTypeInfo responseType = routerResponse.getResponseType();
        if (responseType == null) {
            responseType = matchedRouter.getDefaultResponseType();
        }
        writeChannel(channel, responseType, routerResponse.getContent());
    }

    private static final Logger logger = LoggerFactory.getLogger(ResponseCompletionHandler.class);
}
