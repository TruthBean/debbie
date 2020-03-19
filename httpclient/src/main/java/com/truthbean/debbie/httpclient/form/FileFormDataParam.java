package com.truthbean.debbie.httpclient.form;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

import java.io.File;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-02-24 14:29
 */
public class FileFormDataParam extends FormDataParamName {
    private File file;

    private MediaTypeInfo fileType;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public MediaTypeInfo getFileType() {
        return fileType;
    }

    public void setFileType(MediaTypeInfo fileType) {
        this.fileType = fileType;
    }
}
