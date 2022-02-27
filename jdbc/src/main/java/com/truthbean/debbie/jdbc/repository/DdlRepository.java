/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionService;

import java.util.List;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-13 14:18.
 */
public class DdlRepository implements TransactionService {

    private final DdlRepositoryHandler handler;

    public DdlRepository() {
        this.handler = new DdlRepositoryHandler();
    }

    public int createDatabase(String database) {
        var transaction = getTransaction();
        return handler.createDatabase(getLog(), transaction, database);
    }

    public List<String> showDatabases() {
        var transaction = getTransaction();
        return handler.showDatabases(getLog(), transaction);
    }

    public int dropDatabase(String database) {
        var transaction = getTransaction();
        return handler.dropDatabase(getLog(), transaction, database);
    }

    public int useDatabase(String database) {
        var transaction = getTransaction();
        return handler.dropDatabase(getLog(), transaction, database);
    }

    public List<String> showTables() {
        var transaction = getTransaction();
        return handler.showTables(getLog(), transaction);
    }

    public <E> void createTable(Class<E> entity) {
        var transaction = getTransaction();
        handler.createTable(getLog(), transaction, entity);
    }

    public <E> void createTable(EntityInfo<E> entityInfo) {
        var transaction = getTransaction();
        handler.createTable(getLog(), transaction, entityInfo);
    }

    public void dropTable(String table) {
        var transaction = getTransaction();
        handler.dropTable(getLog(), transaction, table);
    }

    protected Logger getLog() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
