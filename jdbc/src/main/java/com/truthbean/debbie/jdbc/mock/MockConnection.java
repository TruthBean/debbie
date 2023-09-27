package com.truthbean.debbie.jdbc.mock;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockConnection implements Connection {
    private boolean closed;

    private boolean autoCommit;

    private boolean committed;

    private boolean readOnly;

    private String catalog;

    private int transactionIsolation;

    private SQLWarning warning;

    private Map<String, Class<?>> typeMap;

    private int holdability;

    private int savepointId = 0;
    private final Map<Integer, Savepoint> savepointMap = new LinkedHashMap<>();

    private int networkTimeout;

    private String schema;

    private final Properties clientInfo = new Properties();

    @Override
    public Statement createStatement() throws SQLException {
        return new MockStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new MockPreparedStatement(this, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return new MockCallableStatement(this, sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return sql;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        committed = true;
    }

    @Override
    public void rollback() throws SQLException {
        committed = false;
    }

    @Override
    public void close() throws SQLException {
        savepointId = 0;
        savepointMap.clear();
        clientInfo.clear();
        closed = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new MockDatabaseMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return readOnly;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.catalog = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        return catalog;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.transactionIsolation = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return transactionIsolation;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return warning;
    }

    @Override
    public void clearWarnings() throws SQLException {
        warning = null;
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new MockStatement(this, resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new MockPreparedStatement(this, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new MockCallableStatement(this, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return typeMap;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.typeMap = map;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.holdability = holdability;
    }

    @Override
    public int getHoldability() throws SQLException {
        return holdability;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        var savepoint = new MockSavepoint(++savepointId, null);
        savepointMap.put(savepoint.getSavepointId(), savepoint);
        return savepoint;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        var savepoint = new MockSavepoint(++savepointId, name);
        savepointMap.put(savepoint.getSavepointId(), savepoint);
        return savepoint;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        int localSavepointId = savepoint.getSavepointId();
        Set<Integer> deleted = new HashSet<>();
        savepointMap.forEach((id, sp) -> {
            if (id > localSavepointId) {
                deleted.add(id);
            }
        });
        for (Integer id : deleted) {
            savepointMap.remove(id);
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        savepointMap.remove(savepoint.getSavepointId());
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new MockStatement(this, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new MockPreparedStatement(this, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new MockCallableStatement(this, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return new MockPreparedStatement(this, sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new MockPreparedStatement(this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return new MockPreparedStatement(this, sql);
    }

    @Override
    public Clob createClob() throws SQLException {
        return new MockClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return new MockBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return new MockNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return new MockSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return closed;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        clientInfo.put(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        clientInfo.putAll(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return clientInfo.getProperty(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return clientInfo;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return new MockStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.schema = schema;
    }

    @Override
    public String getSchema() throws SQLException {
        return schema;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        close();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.networkTimeout = milliseconds;
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return networkTimeout;
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
