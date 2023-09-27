package com.truthbean.debbie.jdbc.mock;

import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockStruct implements Struct {
    public final String sqlTypeName;
    private final Object[] attributes;

    public MockStruct(final String sqlTypeName, final Object[] attributes) {
        this.sqlTypeName = sqlTypeName;
        this.attributes = attributes;
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return sqlTypeName;
    }

    @Override
    public Object[] getAttributes() throws SQLException {
        final Object[] copy = this.attributes;
        return (copy == null) ? null : Arrays.copyOf(copy, copy.length);
    }

    @Override
    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        final Object[] copy = this.attributes;
        return (copy == null) ? null : Arrays.copyOf(copy, copy.length);
    }
}
