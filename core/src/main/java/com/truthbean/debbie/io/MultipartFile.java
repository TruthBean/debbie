package com.truthbean.debbie.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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