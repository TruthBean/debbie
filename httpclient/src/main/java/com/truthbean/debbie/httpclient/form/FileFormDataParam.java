/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.form;

import com.truthbean.debbie.io.MediaTypeInfo;

import java.io.File;

/**
 * A multipart form-data parameter representing a file upload, carrying
 * the {@link File} and its content type.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-02-24 14:29
 */
public class FileFormDataParam extends FormDataParam {
    /** the file to upload */
    private File file;

    /** the media type of the file content */
    private MediaTypeInfo fileType;

    /** Returns the file to upload. */
    public File getFile() {
        return file;
    }

    /** Sets the file to upload. */
    public void setFile(File file) {
        this.file = file;
    }

    /** Returns the media type of the file content. */
    public MediaTypeInfo getFileType() {
        return fileType;
    }

    /** Sets the media type of the file content. */
    public void setFileType(MediaTypeInfo fileType) {
        this.fileType = fileType;
    }
}
