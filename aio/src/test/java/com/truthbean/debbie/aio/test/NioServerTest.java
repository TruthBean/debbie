/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio.test;

import com.truthbean.Logger;
import com.truthbean.logger.LogLevel;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-29 14:42
 */
public class NioServerTest {
    public static void main(String[] args) throws IOException {
        start();
    }

    private static void start() throws IOException {
        // 创建一个socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 绑定端口
        serverSocketChannel.bind(new InetSocketAddress(16666), 1024);
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 创建 selector
        Selector selector = Selector.open();
        // 注册 selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 循环等待客户端请求
            if (selector.select(1000) == 0) {
                LOGGER.debug("wait 1s for client's connection");
                continue;
            }

            var selectedKeys = selector.selectedKeys();
            var iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                var key = iterator.next();
                if (key.isConnectable()) {
                    System.out.println("a client connection...");
                }
                if (key.isAcceptable()) {
                    // accept event
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);

                    // read event
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if (key.isReadable()) {
                    // 通过key获取注册时生成的socketChannel
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    // 获取buffer
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    socketChannel.read(byteBuffer);
                    System.out.println("client message: " + new String(byteBuffer.array()));
                    byteBuffer.flip();
                    byteBuffer.clear();

                    // write event
                    // ....
                }
                if (key.isWritable()) {

                }
                iterator.remove();
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LogLevel.DEBUG, NioServerTest.class);
}
