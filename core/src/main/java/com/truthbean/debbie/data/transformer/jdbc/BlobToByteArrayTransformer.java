package com.truthbean.debbie.data.transformer.jdbc;

import com.truthbean.debbie.data.transformer.DataTransformer;

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
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Blob reverse(byte[] bytes) {
        return null;
    }
}
