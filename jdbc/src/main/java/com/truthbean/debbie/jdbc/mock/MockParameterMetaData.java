package com.truthbean.debbie.jdbc.mock;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockParameterMetaData implements ParameterMetaData {
    private final Map<Integer, Object> params;

    public MockParameterMetaData(final Map<Integer, Object> params) {
        this.params = params;
    }

    @Override
    public int getParameterCount() throws SQLException {
        return params.size();
    }

    @Override
    public int isNullable(int param) throws SQLException {
        if (!params.containsKey(param)) {
            return ParameterMetaData.parameterNullableUnknown;
        }
        Object o = params.get(param);
        if (o == null) {
            return ParameterMetaData.parameterNullable;
        }
        return ParameterMetaData.parameterNoNulls;
    }

    @Override
    public boolean isSigned(int param) throws SQLException {
        return false;
    }

    @Override
    public int getPrecision(int param) throws SQLException {
        return 0;
    }

    @Override
    public int getScale(int param) throws SQLException {
        return 0;
    }

    @Override
    public int getParameterType(int param) throws SQLException {
        return 0;
    }

    @Override
    public String getParameterTypeName(int param) throws SQLException {
        return null;
    }

    @Override
    public String getParameterClassName(int param) throws SQLException {
        return null;
    }

    @Override
    public int getParameterMode(int param) throws SQLException {
        return ParameterMetaData.parameterModeUnknown;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
