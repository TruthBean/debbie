package com.truthbean.debbie.jdbc.mock;

import java.sql.*;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockStatement implements Statement {

    private final Connection connection;
    private final int resultSetType;
    private final int resultSetConcurrency;
    private final int resultSetHoldability;

    private volatile boolean closed;

    private int maxFieldSize;
    private int maxRows;

    private boolean escapeProcessing;

    private int queryTimeout;

    private String cursorName;

    private int fetchDirection;

    private boolean poolable;

    private int fetchSize;

    private long largeMaxRows;

    public MockStatement(final Connection connection) {
        this.connection = connection;
        this.resultSetType = ResultSet.TYPE_FORWARD_ONLY;
        this.resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
        this.resultSetHoldability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
        this.closed = false;
    }

    public MockStatement(final Connection connection, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        this.connection = connection;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.resultSetHoldability = resultSetHoldability;
        this.closed = false;
    }

    private List<MockColumn> mockData(int length) {
        List<MockColumn> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            MockColumn column = new MockColumn();
            column.setColumnIndex(i);
            list.add(column);
        }
        return list;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        List<List<MockColumn>> data = new ArrayList<>();
        Random random = new Random();
        int size = random.nextInt(1000);
        int length = random.nextInt(20);
        for (int i = 0; i < size; i++) {
            data.add(mockData(length));
        }
        return new MockResultSet(data.iterator());
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return 0;
    }

    @Override
    public void close() throws SQLException {
        this.closed = true;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return maxFieldSize;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        this.maxFieldSize = max;
    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.maxRows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.escapeProcessing = enable;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return queryTimeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        this.queryTimeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {
        this.cursorName = name;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return new MockResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.fetchDirection = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return fetchDirection;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return resultSetConcurrency;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return resultSetType;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        var id = new MockColumn();
        id.setColumnIndex(0);
        id.setData(0L);
        id.setColumnLabel("id");
        id.setColumnIndex(0);
        id.setDataType(Long.class);
        return new MockResultSet(List.of(List.of(id)).iterator());
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return resultSetHoldability;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.poolable = poolable;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return poolable;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        closed = true;
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return closed;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        return 0L;
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        largeMaxRows = max;
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        return largeMaxRows;
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        return new long[0];
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return 0L;
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0L;
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0L;
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0L;
    }

    @Override
    public String enquoteLiteral(String val) throws SQLException {
        return Statement.super.enquoteLiteral(val);
    }

    @Override
    public String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException {
        return Statement.super.enquoteIdentifier(identifier, alwaysQuote);
    }

    @Override
    public boolean isSimpleIdentifier(String identifier) throws SQLException {
        return Statement.super.isSimpleIdentifier(identifier);
    }

    @Override
    public String enquoteNCharLiteral(String val) throws SQLException {
        return Statement.super.enquoteNCharLiteral(val);
    }
}
