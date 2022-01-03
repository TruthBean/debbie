/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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
import com.truthbean.debbie.jdbc.entity.EntityResolver;

public class DebbieRepositoryTemplate<Entity, Id> extends JdbcTransactionRepository<Entity, Id> {

    public DebbieRepositoryTemplate(Class<Id> idClass, Class<Entity> entityClass,
                                    EntityResolver entityResolver, Logger logger) {
        setIdClass(idClass);
        setEntityClass(entityClass);
        setEntityResolver(entityResolver);
        setLogger(logger);
    }

    public DebbieRepositoryTemplate(Class<? extends DebbieRepositoryTemplate<Entity, Id>> repositoryClass,
                                    EntityResolver entityResolver) {
        setRepositoryClass(repositoryClass);
        setEntityResolver(entityResolver);
        setLogger(LoggerFactory.getLogger(repositoryClass));
    }
}