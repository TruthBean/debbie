/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.core.lang.NonNull;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.domain.Sort;
import com.truthbean.core.util.StringUtils;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.entity.EntityResolver;

import java.util.Collection;
import java.util.List;

public class DebbieRepository<Domain, ID> extends CustomRepository<Domain, ID> {
    private static final Logger log = LoggerFactory.getLogger(DebbieRepository.class);

    public DebbieRepository() {
        // do nothing
    }

    public DebbieRepository(Class<ID> idClass, Class<Domain> entityClass,
                            EntityResolver entityResolver) {
        super(new JdbcTransactionRepository<>());
        setIdClass(idClass);
        setEntityClass(entityClass);
        setEntityResolver(entityResolver);
    }

    /**
     * Finds all domain by id list and the specified sort.
     *
     * @param ids  id list of domain must not be null
     * @param sort the specified sort must not be null
     * @return a list of domains
     */
    public List<Domain> findAllByIdIn(Collection<ID> ids, Sort sort) {
        String whereSql = "where id in (?) order by " + sort.toString();
        return super.findList(whereSql, StringUtils.joining(ids, ","));
    }

    /**
     * Finds all domain by id list and order by desc.
     *
     * @param column column name must not be null
     * @return a list of domains
     */
    public List<Domain> findListOrderByDesc(@NonNull String column) {
        String orderSql = "order by " + column + " desc";
        return super.findList(orderSql);
    }

    /**
     * Finds all domain by id list and order by asc.
     *
     * @param column column name must not be null
     * @return a list of domains
     */
    public List<Domain> findListOrderByAsc(@NonNull String column) {
        String orderSql = "order by " + column + " asc";
        return super.findList(orderSql);
    }

    /**
     * Finds column data from table.
     *
     * @param tableName table name must not be null
     * @param columnName column name must not be null
     * @param type type of column must not be null
     * @return a list of column data
     * @param <T>
     */
    public <T> List<T> findColumn(@NonNull String tableName, @NonNull String columnName, @NonNull Class<T> type) {
        String sql = "select " + columnName + " from " + tableName;
        return super.select(sql, type);
    }

    /**
     * Finds all domain by id list and the specified pageable.
     *
     * @param ids      id list of domain must not be null
     * @param pageable the specified pageable must not be null
     * @return a list of domains
     */
    public Page<Domain> findAllByIdIn(Collection<ID> ids, PageRequest pageable) {
        String whereSql = "where id in (?)";
        return super.findPaged(pageable, whereSql, StringUtils.joining(ids, ","));
    }

    /**
     * Deletes by id list.
     *
     * @param ids id list of domain must not be null
     * @return number of rows affected
     */
    public long deleteByIdIn(Iterable<ID> ids) {
        log.debug("Customized deleteByIdIn method was invoked");
        var ref = new Object() {
            long l = 0L;
        };
        var transactionInfo = getTransaction();
        transactionInfo.setAutoCommit(false);
        transactionInfo.startSession();
        try {
            ids.forEach(id -> {
                if (super.deleteById(id)) {
                    ref.l++;
                }
            });
            transactionInfo.commit();
        } catch (Exception e) {
            transactionInfo.rollback();
            throw e;
        } finally {
            transactionInfo.endSession();
        }

        return ref.l;
    }

    /**
     * Deletes by column name.
     *
     * @param columnName column name must not be null
     * @return number of rows affected
     */
    public long deleteTableByColumn(@NonNull String tableName, @NonNull String columnName, Object columnValue) {
        log.debug("Customized deleteTableByColumn method was invoked");
        var transactionInfo = getTransaction();
        transactionInfo.setAutoCommit(false);
        transactionInfo.startSession();
        long result = 0L;
        try {
            var sql = "delete from " + tableName + " where " + columnName + " = ?";
            result = delete(sql, columnValue);
            transactionInfo.commit();
        } catch (Exception e) {
            transactionInfo.rollback();
            throw e;
        } finally {
            transactionInfo.setAutoCommit(true);
            transactionInfo.endSession();
        }

        return result;
    }
}