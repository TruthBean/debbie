package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.transaction.TransactionService;

import java.util.List;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-13 14:18.
 */
public class DdlRepository implements TransactionService {

    private final DataSourceConfiguration configuration;
    private final DdlRepositoryHandler handler;

    public DdlRepository(DataSourceConfiguration configuration) {
        this.configuration = configuration;
        this.handler = new DdlRepositoryHandler();
        this.handler.setDriverName(configuration.getDriverName());
    }

    public int createDatabase(String database) throws TransactionException {
        var connection = getDriverConnection();
        return handler.createDatabase(connection, database);
    }

    public List<String> showDatabases() {
        var connection = getDriverConnection();
        return handler.showDatabases(connection);
    }

    public int dropDatabase(String database) throws TransactionException {
        var connection = getDriverConnection();
        return handler.dropDatabase(connection, database);
    }

    public int useDatabase(String database) throws TransactionException {
        var connection = getDriverConnection();
        return handler.dropDatabase(connection, database);
    }

    public List<String> showTables() {
        var connection = getDriverConnection();
        return handler.showTables(connection);
    }

    public <E> void createTable(Class<E> entity) throws TransactionException {
        var connection = getDriverConnection();
        handler.createTable(connection, entity);
    }

    public <E> void createTable(EntityInfo<E> entityInfo) throws TransactionException {
        var connection = getDriverConnection();
        handler.createTable(connection, entityInfo);
    }

    public void dropTable(String table) throws TransactionException {
        var connection = getDriverConnection();
        handler.dropTable(connection, table);
    }
}
