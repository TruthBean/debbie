/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.entity.SqlEntityNullException;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class JdbcTransactionRepository<Entity, Id> extends JdbcRepositoryHandler {

    private Class<Id> idClass;
    private Class<Entity> entityClass;
    private Class<? extends JdbcTransactionRepository<Entity, Id>> repositoryClass;

    private EntityResolver entityResolver;

    private Logger logger;

    void setIdClass(Class<Id> idClass) {
        this.idClass = idClass;
    }

    void setEntityClass(Class<Entity> entityClass) {
        this.entityClass = entityClass;
    }

    void setRepositoryClass(Class<? extends JdbcTransactionRepository<Entity, Id>> repositoryClass) {
        this.repositoryClass = repositoryClass;
    }

    void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @SuppressWarnings("unchecked")
    protected Class<Entity> getEntityClass() {
        if (entityClass == null) {
            try {
                Class<?> clazz = repositoryClass;
                if (clazz == null) {
                    clazz = getClass();
                }
                var types = ReflectionHelper.getActualTypes(clazz);
                if (types != null && types.length == 2) {
                    entityClass = (Class<Entity>) types[0];
                }
            } catch (Exception e) {
                LOGGER.error("getActualTypeArguments error. ", e);
            }
            if (entityClass == null) {
                throw new SqlEntityNullException("get entity class error");
            }
        }
        return entityClass;
    }

    @SuppressWarnings("unchecked")
    protected Class<Id> getIdClass() {
        if (idClass == null) {
            try {
                Class<?> clazz = repositoryClass;
                if (clazz == null) {
                    clazz = getClass();
                }
                var types = ReflectionHelper.getActualTypes(clazz);
                if (types != null && types.length == 2) {
                    idClass = (Class<Id>) types[1];
                }
            } catch (Exception e) {
                LOGGER.error("getActualTypeArguments error. ", e);
            }
            if (idClass == null) {
                throw new SqlEntityNullException("get id class error");
            }
        }
        return idClass;
    }

    protected <T> DmlRepositoryHandler.ConditionAndValue resolveCondition(DataSourceDriverName driverName, T condition, boolean withNull) {
        if (condition != null) {
            var conditionInfo = entityResolver.resolveEntity(driverName, condition);
            return super.resolveCondition(conditionInfo, withNull);
        }
        return new DmlRepositoryHandler.ConditionAndValue();
    }

    public boolean deleteById(TransactionInfo transaction, Id id) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.deleteById(logger, transaction, entityInfo, id);
    }

    public boolean deleteByColumn(TransactionInfo transaction, String columnName, Object value) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.deleteByColumn(logger, transaction, entityInfo, columnName, value);
    }

    public int deleteByIdIn(TransactionInfo transaction, List<Id> ids) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.deleteByIdIn(logger, transaction, entityInfo, ids);
    }

    public <C> int deleteByColumnIn(TransactionInfo transaction, String columnName, List<C> values) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.deleteByColumnIn(logger, transaction, entityInfo, columnName, values);
    }

    public int delete(TransactionInfo transaction, Entity condition, boolean withConditionNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        entityInfo.resolve(condition);
        return super.delete(logger, transaction, entityInfo, withConditionNull);
    }

    public int deleteAll(TransactionInfo transaction) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.deleteAll(logger, transaction, entityInfo.getTable());
    }

    public Id insert(TransactionInfo transaction, Entity entity, boolean withEntityPropertyNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        entityInfo.resolve(entity);
        return super.insert(logger, transaction, entityInfo, withEntityPropertyNull);
    }

    public int insert(TransactionInfo transaction, Collection<Entity> entities, boolean withEntityPropertyNull) {
        return super.insert(logger, transaction, entityResolver, entityClass, entities, withEntityPropertyNull);
    }

    public boolean update(TransactionInfo transaction, Entity entity, boolean withEntityPropertyNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        entityInfo.resolve(entity);
        return super.update(logger, transaction, entityInfo, withEntityPropertyNull);
    }

    public int update(TransactionInfo transaction, Entity entity, boolean withEntityPropertyNull, String whereSql, Object... args) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        entityInfo.resolve(entity);
        return super.update(logger, transaction, entityInfo, withEntityPropertyNull, whereSql, args);
    }

    public <S extends Entity> S save(TransactionInfo transaction, S entity) {
        return super.save(logger, transaction, entityResolver, entityClass, entity);
    }

    public Entity findByColumn(TransactionInfo transaction, String columnName, Object value) {
        return super.findByColumn(logger, transaction, entityResolver, entityClass, columnName, value);
    }

    public List<Entity> findListByColumn(TransactionInfo transaction, String columnName, Object value) {
        return super.findListByColumn(logger, transaction, entityResolver, entityClass, columnName, value);
    }

    public List<Entity> findListByColumnIn(TransactionInfo transaction, String columnName, Collection<?> values) {
        return super.findListByColumnIn(logger, transaction, entityResolver, entityClass, columnName, values);
    }

    public Entity findOne(TransactionInfo transaction, Entity condition, boolean withConditionNull) {
        return super.findOne(logger, transaction, entityResolver, entityClass, condition, withConditionNull);
    }

    public Entity findOne(TransactionInfo transaction, String whereSql, Object... args) {
        return super.findOne(logger, transaction, entityResolver, entityClass, whereSql, args);
    }

    public <T> T queryOne(TransactionInfo transaction, String selectSql, Class<T> clazz, Object... args) {
        return super.queryOne(logger, transaction, entityResolver, selectSql, clazz, args);
    }

    public Optional<Entity> findOptional(TransactionInfo transaction, Entity condition, boolean withConditionNull) {
        return super.findOptional(logger, transaction, entityResolver, entityClass, condition, withConditionNull);
    }

    public Optional<Entity> findOptional(TransactionInfo transaction, String whereSql, Object... args) {
        return super.findOptional(logger, transaction, entityResolver, entityClass, whereSql, args);
    }

    public List<Entity> findList(TransactionInfo transaction, Entity condition, boolean withConditionNull) {
        return super.findList(logger, transaction, entityResolver, entityClass, condition, withConditionNull);
    }

    public List<Entity> findList(TransactionInfo transaction, String whereSql, Object... args) {
        return super.findList(logger, transaction, entityResolver, entityClass, whereSql, args);
    }

    public <T> List<T> query(final TransactionInfo transaction, String selectSql, Class<T> clazz, Object... args) {
        return super.query(logger, transaction, entityResolver, selectSql, clazz, args);
    }

    public <E> Page<E> selectPaged(TransactionInfo transaction, EntityInfo<E> entityInfo,
                                                  E condition, boolean withNull, PageRequest pageable) {
        return super.selectPaged(logger, transaction, entityResolver, entityInfo, condition, withNull, pageable);
    }

    public <E> Page<E> selectPaged(TransactionInfo transaction, EntityInfo<E> entityInfo,
                                   PageRequest pageable, String whereSql, Object... args) {
        return super.selectPaged(logger, transaction, entityResolver, entityInfo, pageable, whereSql, args);
    }

    public <E> Page<E> selectPaged(TransactionInfo transaction, EntityInfo<E> entityInfo, PageRequest pageable) {
        return selectPaged(transaction, entityInfo, pageable, null);
    }

    public <E, ID> Optional<E> selectOptionalById(TransactionInfo transaction, EntityInfo<E> entityInfo, ID id) {
        return selectOptionalById(logger, transaction, entityResolver, entityInfo, id);
    }

    public Page<Entity> findPaged(TransactionInfo transaction, Entity condition, boolean withConditionNull, PageRequest pageable) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return selectPaged(transaction, entityInfo, condition, withConditionNull, pageable);
    }

    public Page<Entity> findPaged(TransactionInfo transaction, PageRequest pageable, String whereSql, Object... args) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return selectPaged(transaction, entityInfo, pageable, whereSql, args);
    }

    public Page<Entity> findPaged(TransactionInfo transaction, PageRequest pageable) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return selectPaged(transaction, entityInfo, pageable, null);
    }

    public List<Entity> findAll(TransactionInfo transaction) {
        return super.findAll(logger, transaction, entityResolver, entityClass);
    }

    public List<Entity> findAllByIdIn(TransactionInfo transaction, Collection<Id> ids) {
        return super.findAllByIdIn(logger, transaction, entityResolver, entityClass, ids);
    }

    public Long count(TransactionInfo transaction, Entity condition, boolean withConditionNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.count(logger, transaction, entityInfo, condition, withConditionNull);
    }

    public Long count(TransactionInfo transaction) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.count(logger, transaction, entityInfo);
    }

    public Entity findById(TransactionInfo transaction, Id id) {
        return super.findById(logger, transaction, entityResolver, entityClass, id);
    }

    public boolean existsById(TransactionInfo transaction, Id id) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.existsById(logger, transaction, entityInfo, id);
    }

    public Optional<Entity> queryOptionalById(TransactionInfo transaction, Id id) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return selectOptionalById(transaction, entityInfo, id);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTransactionRepository.class);

}
