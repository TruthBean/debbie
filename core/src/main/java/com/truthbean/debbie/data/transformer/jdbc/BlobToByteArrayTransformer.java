/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.jdbc;

import com.truthbean.debbie.data.transformer.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-03 21:08
 */
public class BlobToByteArrayTransformer implements DataTransformer<Blob, byte[]> {
    @Override
    public byte[] transform(Blob blob) {
        try {
            return blob.getBytes(1L, (int) blob.length());
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    @Override
    public Blob reverse(byte[] bytes) {
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BlobToByteArrayTransformer.class);
}
