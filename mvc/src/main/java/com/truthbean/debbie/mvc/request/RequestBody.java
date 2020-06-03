/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.StreamHelper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-11 14:28
 */
public class RequestBody {
    private byte[] bytes;

    private InputStream inputStream;

    private ByteBuffer byteBuffer;

    private BufferedReader bufferedReader;

    private List<String> content;

    public RequestBody(byte[] bytes) {
        this.bytes = bytes;
        this.inputStream = new ByteArrayInputStream(bytes);
        this.bufferedReader = StreamHelper.toBufferedReader(bytes);
        getContent();
    }

    public RequestBody(InputStream inputStream) {
        this.inputStream = inputStream;
        this.bytes = StreamHelper.toByteArray(inputStream);
        this.bufferedReader = StreamHelper.toBufferedReader(inputStream);
        getContent();
    }

    public RequestBody(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        //TODO
        getContent();
    }

    RequestBody(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public List<String> getContent() {
        if (content == null) {
            try {
                List<String> result = new ArrayList<>();
                String line;
                while ((line = this.bufferedReader.readLine()) != null) {
                    result.add(line);
                }
                content = result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

}
