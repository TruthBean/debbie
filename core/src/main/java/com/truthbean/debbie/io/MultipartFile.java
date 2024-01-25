/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 21:18
 */
public class MultipartFile {
    private String fileName;

    private MediaTypeInfo contentType;

    private byte[] content;
    private InputStream inputStream;

    private String fileExt;

    public MultipartFile() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MediaTypeInfo getContentType() {
        return contentType;
    }

    public void setContentType(MediaTypeInfo contentType) {
        this.contentType = contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType.info();
    }

    public byte[] getContent() {
        return content;
    }

    public InputStream getInputStream() {
        if (inputStream == null && content != null) {
            inputStream = new ByteArrayInputStream(content);
        }
        return inputStream;
    }

    public void transferTo(File dest) throws IOException {
        StreamHelper.copy(getInputStream(), Files.newOutputStream(dest.toPath()));
    }

    public void transferTo(Path dest) throws IOException {
        StreamHelper.copy(getInputStream(), Files.newOutputStream(dest));
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    @Override
    public String toString() {
        return "{" + "fileName:\"" + fileName + "\"" + "," + "contentType:" + contentType + "," + "content:" + Arrays.toString(content) + "," + "fileExt:\"" + fileExt + "\"" + "}";
    }
}