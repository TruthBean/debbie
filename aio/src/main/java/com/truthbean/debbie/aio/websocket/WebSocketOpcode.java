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

/**
 * WebSocket frame opcodes defined in RFC 6455 section 5.2.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public enum WebSocketOpcode {
    /** continuation frame for fragmented messages */
    CONTINUATION(0x0),
    /** text data frame */
    TEXT(0x1),
    /** binary data frame */
    BINARY(0x2),
    /** connection close frame */
    CLOSE(0x8),
    /** ping frame */
    PING(0x9),
    /** pong frame */
    PONG(0xA);

    private final int value;

    WebSocketOpcode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Resolves an opcode from its 4-bit wire value.
     *
     * @param value the opcode value read from the frame header
     * @return the matching opcode, or {@code null} for reserved/unknown values
     */
    public static WebSocketOpcode of(int value) {
        for (WebSocketOpcode opcode : values()) {
            if (opcode.value == value) {
                return opcode;
            }
        }
        return null;
    }
}
