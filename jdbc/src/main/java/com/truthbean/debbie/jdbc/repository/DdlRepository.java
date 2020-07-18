/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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

    // private final DataSourceConfiguration configuration;
    private final DdlRepositoryHandler handler;

    public DdlRepository(DataSourceConfiguration configuration) {
        // this.configuration = configuration;
        this.handler = new DdlRepositoryHandler();
        this.handler.setDriverName(configuration.getDriverName());
    }

    public int createDatabase(String database) {
        var connection = getDriverConnection();
        return handler.createDatabase(connection, database);
    }

    public List<String> showDatabases() {
        var connection = getDriverConnection();
        return handler.showDatabases(connection);
    }

    public int dropDatabase(String database) {
        var connection = getDriverConnection();
        return handler.dropDatabase(connection, database);
    }

    public int useDatabase(String database) {
        var connection = getDriverConnection();
        return handler.dropDatabase(connection, database);
    }

    public List<String> showTables() {
        var connection = getDriverConnection();
        return handler.showTables(connection);
    }

    public <E> void createTable(Class<E> entity) {
        var connection = getDriverConnection();
        handler.createTable(connection, entity);
    }

    public <E> void createTable(EntityInfo<E> entityInfo) {
        var connection = getDriverConnection();
        handler.createTable(connection, entityInfo);
    }

    public void dropTable(String table) {
        var connection = getDriverConnection();
        handler.dropTable(connection, table);
    }
}
