/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
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
        String whereSql = "id in (?) order by " + sort.toString();
        return super.findList(whereSql, StringUtils.joining(ids, ","));

    }

    public Page<Domain> findAllByIdIn(Collection<ID> ids, PageRequest pageable) {
        String whereSql = "id in (?)";
        return super.findPaged(pageable, whereSql, StringUtils.joining(ids, ","));
    }

    /**
     * Deletes by id list.
     *
     * @param ids id list of domain must not be null
     * @return number of rows affected
     */
    @JdbcTransactional(readonly = false)
    public long deleteByIdIn(Iterable<ID> ids) {
        log.debug("Customized deleteByIdIn method was invoked");
        var ref = new Object() {
            long l = 0L;
        };
        ids.forEach(id -> {
            if (super.deleteById(id)) {
                ref.l++;
            }
        });
        return ref.l;
    }
}