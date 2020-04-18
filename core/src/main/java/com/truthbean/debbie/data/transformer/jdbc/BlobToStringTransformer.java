package com.truthbean.debbie.data.transformer.jdbc;

import com.truthbean.debbie.data.transformer.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
