package com.truthbean.debbie.jdbc.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockClob implements Clob {
    @Override
    public long length() throws SQLException {
        return 0;
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return null;
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return null;
    }

    @Override
    public long position(String searchstr, long start) throws SQLException {
        return 0;
    }

    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        return 0;
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        return 0;
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return 0;
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        return null;
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        return null;
    }

    @Override
    public void truncate(long len) throws SQLException {

    }

    @Override
    public void free() throws SQLException {

    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return null;
    }
}
