/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
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
import com.truthbean.logger.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-28 11:31
 */
public class BioServerTest {
    public static void main(String[] args) throws IOException {
        start();
    }

    private static void start() throws IOException {
        final ExecutorService service = Executors.newCachedThreadPool();
        final ServerSocket socket = new ServerSocket(6666);


        while (true) {
            LOGGER.debug(() -> "waiting for client's connection ....");
            final Socket accept = socket.accept();
            service.execute(() -> {
                try {
                    LOGGER.debug("handle a client request ....");
                    final InputStream inputStream = accept.getInputStream();
                    final var length = 1024;
                    final byte[] bytes = new byte[length];
                    final List<String> content = new ArrayList<>();
                    int read = -1;
                    // read时阻塞，如果没有消息就会一直等着，所以不能用while
                    if ((read = inputStream.read(bytes, 0, length)) != -1) {
                        var line = new String(bytes, 0, read);
                        content.add(line);
                    }
                    LOGGER.debug("finish read.");
                    for (String s : content) {
                        System.out.println(s);
                    }
                    LOGGER.debug("response to the client...");
                    final OutputStream outputStream = accept.getOutputStream();
                    outputStream.write("Thank you, sir! Your are good man...... Bye!".getBytes());
                    outputStream.flush();

                    // 结束会话
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LogLevel.DEBUG, BioServerTest.class);
}
