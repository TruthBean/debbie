package com.truthbean.debbie.aio;

import com.truthbean.debbie.mvc.request.RouterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * CompletionHandler&lt;V,A&gt;
 *     V-IO操作的结果，这里是read操作成功读取的字节数
 *     A-IO操作附件，由于ConnectCompleteHandler中调用asyncSocketChannel.read方法时
 *     传入了ByteBuffer，所以这里为ByteBuffer
 *
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:13
 */
public class RequestCompleteHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RequestCompleteHandler.class);

    RouterRequest handle(AsynchronousSocketChannel channel) {
        try {
            // ByteBuffer是非线程安全的，如果要在多个线程间共享同一个ByteBuffer，需要考虑线程安全性问题
            var readByteBuffer = ByteBuffer.allocate(2048);
            var read = channel.read(readByteBuffer);
            SocketAddress remoteAddress = channel.getRemoteAddress();
            return completed(read.get(), readByteBuffer, remoteAddress);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RouterRequest completed(int result, final ByteBuffer readByteBuffer, final SocketAddress remoteAddress) {
        LOG.info("Deal thread of [RequestCompleteHandler] : " + Thread.currentThread().getName());
        LOG.info("Read bytes : " + result);
        if (result == -1) {
            LOG.warn("httpRequest from client error!");
            return null;
        } else {
            readByteBuffer.flip();
            var remaining = readByteBuffer.remaining();
            var reqBytes = new byte[remaining];
            readByteBuffer.get(reqBytes);

            var originRequest = new String(reqBytes);
            LOG.trace("raw request: " + originRequest);

            List<String> lines = originRequest.lines().collect(Collectors.toList());
            if (lines.isEmpty() || lines.size() == 1)
                return null;

            return new RawRequestWrapper(lines, remoteAddress);
        }
    }
}
