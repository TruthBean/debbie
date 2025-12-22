/**
 * Copyright (c) 2025 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
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
    public List<Domain> findListOrderByDesc(String column) {
        String whereSql = "order by " + column + " desc";
        return super.findList(whereSql);
    }

    /**
     * Finds all domain by id list and order by asc.
     *
     * @param column column name must not be null
     * @return a list of domains
     */
    public List<Domain> findListOrderByAsc(String column) {
        String whereSql = "order by " + column + " asc";
        return super.findList(whereSql);
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
        } catch (Exception e) {
            transactionInfo.rollback();
            throw e;
        }
        transactionInfo.commit();
        transactionInfo.endSession();

        return ref.l;
    }
}