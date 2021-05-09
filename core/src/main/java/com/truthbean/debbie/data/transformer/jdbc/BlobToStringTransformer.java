/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.jdbc;

import com.truthbean.transformer.DataTransformer;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-03 21:10
 */
public class BlobToStringTransformer implements DataTransformer<Blob, String> {
    @Override
    public String transform(Blob blob) {
        try {
            return new String(blob.getBytes(1L, (int) blob.length()));
        } catch (SQLException e) {
            logger.error("", e);
        }
        return null;
    }

    @Override
    public Blob reverse(String bytes) {
        return null;
    }

    private static final Logger logger = LoggerFactory.getLogger(BlobToStringTransformer.class);
}
