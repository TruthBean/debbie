package com.truthbean.debbie.core.io;

import java.util.Arrays;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-08 21:18
 */
public class MultipartFile {
    private String fileName;

    private MediaType contentType;

    private byte[] content;

    private String fileExt;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
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