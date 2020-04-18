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
