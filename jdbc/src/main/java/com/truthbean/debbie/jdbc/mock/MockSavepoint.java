package com.truthbean.debbie.jdbc.mock;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockSavepoint implements Savepoint {
    private final int id;
    private final String name;

    public MockSavepoint(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getSavepointId() throws SQLException {
        return id;
    }

    @Override
    public String getSavepointName() throws SQLException {
        return name;
    }
}
