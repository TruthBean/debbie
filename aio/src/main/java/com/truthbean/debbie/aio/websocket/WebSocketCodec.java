/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio.websocket;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * RFC 6455 frame codec for the AIO server, implemented purely on the JDK
 * {@link AsynchronousSocketChannel} API. Client frames must be masked and are
 * unmasked here; server frames are written unmasked as required by the spec.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
final class WebSocketCodec {

    /** largest payload length that fits in the 7-bit header field */
    private static final int MAX_SINGLE_BYTE_LENGTH = 125;
    /** payload length escaped marker for the 16-bit extended length field */
    private static final int LENGTH_16_BIT = 126;
    /** payload length escaped marker for the 64-bit extended length field */
    private static final int LENGTH_64_BIT = 127;
    /** hard limit for one message payload, 16 MiB */
    private static final long MAX_PAYLOAD_SIZE = 16L * 1024 * 1024;

    private WebSocketCodec() {
    }

    /**
     * Reads and decodes exactly one frame from the channel, blocking up to
     * the given timeout for each socket read.
     *
     * @param channel the connection to read from
     * @param timeout read timeout in milliseconds, {@code <= 0} waits indefinitely
     * @return the decoded frame, or {@code null} when the peer closed the
     *         connection before any byte of a new frame arrived
     * @throws IOException  on read failure or protocol violation
     * @throws InterruptedException if the waiting thread is interrupted
     */
    static WebSocketFrame readFrame(AsynchronousSocketChannel channel, long timeout) throws IOException, InterruptedException {
        ByteBuffer header = ByteBuffer.allocate(2);
        if (!readFully(channel, header, timeout)) {
            return null;
        }
        header.flip();
        byte first = header.get();
        byte second = header.get();

        boolean fin = (first & 0x80) != 0;
        if ((first & 0x70) != 0) {
            throw new IOException("WebSocket protocol error: reserved bits must be zero");
        }
        WebSocketOpcode opcode = WebSocketOpcode.of(first & 0x0F);
        if (opcode == null) {
            throw new IOException("WebSocket protocol error: unknown opcode " + (first & 0x0F));
        }

        boolean masked = (second & 0x80) != 0;
        long length = Byte.toUnsignedInt(second) & 0x7F;
        if (length == LENGTH_16_BIT) {
            ByteBuffer ext = ByteBuffer.allocate(2);
            if (!readFully(channel, ext, timeout)) {
                throw new EOFException("WebSocket frame extended length truncated");
            }
            ext.flip();
            length = Short.toUnsignedInt(ext.getShort());
        } else if (length == LENGTH_64_BIT) {
            ByteBuffer ext = ByteBuffer.allocate(8);
            if (!readFully(channel, ext, timeout)) {
                throw new EOFException("WebSocket frame extended length truncated");
            }
            ext.flip();
            length = ext.getLong();
        }
        if (length < 0 || length > MAX_PAYLOAD_SIZE) {
            throw new IOException("WebSocket frame payload size illegal: " + length);
        }

        byte[] maskKey = null;
        if (masked) {
            ByteBuffer key = ByteBuffer.allocate(4);
            if (!readFully(channel, key, timeout)) {
                throw new EOFException("WebSocket frame mask key truncated");
            }
            maskKey = key.array();
        }

        byte[] payload = new byte[(int) length];
        if (length > 0) {
            ByteBuffer payloadBuffer = ByteBuffer.wrap(payload);
            if (!readFully(channel, payloadBuffer, timeout)) {
                throw new EOFException("WebSocket frame payload truncated");
            }
            if (masked) {
                for (int i = 0; i < payload.length; i++) {
                    payload[i] = (byte) (payload[i] ^ maskKey[i % 4]);
                }
            }
        }
        return new WebSocketFrame(fin, opcode, payload, masked);
    }

    /**
     * Encodes a frame and writes it to the channel, blocking until the write
     * completes or the timeout elapses.
     *
     * @param channel the connection to write to
     * @param frame   the frame to send
     * @param timeout write timeout in milliseconds, {@code <= 0} waits indefinitely
     * @throws IOException on write failure
     * @throws InterruptedException if the waiting thread is interrupted
     */
    static void writeFrame(AsynchronousSocketChannel channel, WebSocketFrame frame, long timeout) throws IOException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.wrap(encode(frame));
        try {
            while (buffer.hasRemaining()) {
                Future<Integer> future = channel.write(buffer);
                if (timeout > 0) {
                    future.get(timeout, TimeUnit.MILLISECONDS);
                } else {
                    future.get();
                }
            }
        } catch (TimeoutException e) {
            throw new IOException("WebSocket frame write timed out", e);
        } catch (java.util.concurrent.ExecutionException e) {
            throw new IOException("WebSocket frame write failed", e.getCause());
        }
    }

    private static byte[] encode(WebSocketFrame frame) {
        byte[] payload = frame.getPayload();
        int length = payload.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream(length + 14);

        int first = (frame.isFin() ? 0x80 : 0x00) | frame.getOpcode().getValue();
        out.write(first);

        if (length <= MAX_SINGLE_BYTE_LENGTH) {
            out.write(length);
        } else if (length <= 0xFFFF) {
            out.write(LENGTH_16_BIT);
            out.write((length >> 8) & 0xFF);
            out.write(length & 0xFF);
        } else {
            out.write(LENGTH_64_BIT);
            long value = length;
            for (int i = 7; i >= 0; i--) {
                out.write((int) ((value >> (8 * i)) & 0xFF));
            }
        }
        // server-to-client frames must not be masked (RFC 6455 section 5.1)
        out.write(payload, 0, payload.length);
        return out.toByteArray();
    }

    /**
     * Fills the destination buffer from the channel, blocking up to the given
     * timeout per read.
     *
     * @return {@code true} when the buffer is full, {@code false} on a clean
     *         end-of-stream before any byte was read into an empty position
     */
    private static boolean readFully(AsynchronousSocketChannel channel, ByteBuffer destination, long timeout)
            throws IOException, InterruptedException {
        while (destination.hasRemaining()) {
            Future<Integer> future = channel.read(destination);
            int read;
            try {
                read = timeout > 0 ? future.get(timeout, TimeUnit.MILLISECONDS) : future.get();
            } catch (TimeoutException e) {
                throw new IOException("WebSocket frame read timed out", e);
            } catch (java.util.concurrent.ExecutionException e) {
                throw new IOException("WebSocket frame read failed", e.getCause());
            }
            if (read <= 0) {
                return false;
            }
        }
        return true;
    }
}
