package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.entity.ResultMap;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/14 23:14.
 */
public class JdbcRepositoryHandler extends DmlRepositoryHandler {

    public <Entity> int insert(Logger logger, TransactionInfo transaction, EntityResolver entityResolver, Class<Entity> entityClass,
                               Collection<Entity> entities, boolean withEntityPropertyNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<EntityInfo<Entity>> list = new ArrayList<>();
        for (Entity entity : entities) {
            entityInfo.resolve(entity);
            list.add(entityInfo);
        }
        return super.insertMany(logger, transaction, list, withEntityPropertyNull);
    }

    public <S extends Entity, Entity, Id> S save(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                             Class<Entity> entityClass, S entity) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        entityInfo.resolve(entity);

        ColumnInfo primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey.getValue() != null) {
            var bool = update(logger, transaction, entityInfo, true);
            if (bool) {
                List<ColumnInfo> list = selectOne(logger, transaction, entityInfo, false);
                return (S) entityResolver.resolve(entity.getClass(), list);
            } else {
                return null;
            }
        } else {
            Id insert = insert(logger, transaction, entityInfo, true);
            entityInfo.getPrimaryKey().setValue(insert);
            List<ColumnInfo> list = selectById(logger, transaction, entityInfo, insert);
            return (S) entityResolver.resolve(entity.getClass(), list);
        }
    }

    public <Id> boolean deleteById(Logger logger, TransactionInfo transaction, String table, Id id) {
        return super.deleteByColumn(logger, transaction, table, "id", id);
    }

    public <Entity, Id> boolean deleteById(Logger logger, TransactionInfo transaction, Id id,
                                           EntityResolver entityResolver, Class<Entity> entityClass) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        return super.deleteById(logger, transaction, entityInfo, id);
    }

    public <Entity, Id> Entity findById(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                        Class<Entity> entityClass, Id id) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<ColumnInfo> list = selectById(logger, transaction, entityInfo, id);
        return entityResolver.resolve(entityClass, list);
    }

    public <E, ID> Optional<E> selectOptionalById(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                                  EntityInfo<E> entityInfo, ID id) {
        List<ColumnInfo> result = selectById(logger, transaction, entityInfo, id);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityResolver.resolve(entityInfo.getJavaType(), result));
    }

    public <Entity, Id> List<Entity> findAllByIdIn(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                                   Class<Entity> entityClass, Collection<Id> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Entity> result = new ArrayList<>();
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<List<ColumnInfo>> list = super.selectByIdIn(logger, transaction, entityInfo, ids);
        for (List<ColumnInfo> columnInfoList : list) {
            result.add(entityResolver.resolve(entityClass, columnInfoList));
        }
        return result;
    }

    public <Entity> Entity findOne(Logger logger, TransactionInfo transaction, EntityResolver entityResolver, Class<Entity> entityClass,
                                   Entity condition, boolean withConditionNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        entityInfo.resolve(condition);
        List<ColumnInfo> list = super.selectOne(logger, transaction, entityInfo, withConditionNull);
        return entityResolver.resolve(entityClass, list);
    }

    public <Entity> Entity findOne(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                   Class<Entity> entityClass,
                                   String whereSql, Object... args) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<ColumnInfo> list = super.selectOne(logger, transaction, entityInfo, whereSql, args);
        return entityResolver.resolve(entityClass, list);
    }

    public <Entity> Entity findByColumn(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                        Class<Entity> entityClass, String columnName, Object value) {
        String whereSql = columnName + " = ?";
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<ColumnInfo> list = super.selectOne(logger, transaction, entityInfo, whereSql, value);
        return entityResolver.resolve(entityClass, list);
    }

    public <Entity> List<Entity> findListByColumn(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                                  Class<Entity> entityClass, String columnName, Object value) {
        String whereSql = columnName + " = ?";
        return findList(logger, transaction, entityResolver, entityClass, whereSql, value);
    }

    public <Entity> List<Entity> findListByColumnIn(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                                    Class<Entity> entityClass, String columnName, Collection<?> values) {
        if (values.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> c = new ArrayList<>();
        Object[] objects = new Object[values.size()];
        int i = 0;
        for (Object value : values) {
            c.add("?");
            objects[i++] = value;
        }
        String s = StringUtils.joining(c, ",");
        String whereSql = columnName + " in (" + s + ")";
        return findList(logger, transaction, entityResolver, entityClass, whereSql, objects);
    }

    public <T> List<T> query(Logger logger, final TransactionInfo transaction, EntityResolver entityResolver,
                             String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> list = super.query(logger, transaction, selectSql, args);
        if (list.isEmpty()) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (List<ColumnInfo> infos : list) {
            result.add(entityResolver.resolve(clazz, infos));
        }
        return result;
    }

    public <T> T queryOne(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                          String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> list = super.query(logger, transaction, selectSql, args);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return entityResolver.resolve(clazz, list.get(0));
        } else {
            throw new MoreRowException("Expect one row, but it has" + list.size() + " rows.");
        }
    }

    public <Entity> Optional<Entity> findOptional(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                                  Class<Entity> entityClass, Entity condition, boolean withConditionNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        entityInfo.resolve(condition);
        List<ColumnInfo> list = super.selectOne(logger, transaction, entityInfo, withConditionNull);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entityResolver.resolve(entityClass, list));
        }
    }

    public <Entity> Optional<Entity> findOptional(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                                  Class<Entity> entityClass, String whereSql, Object... args) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<ColumnInfo> list = super.selectOne(logger, transaction, entityInfo, whereSql, args);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entityResolver.resolve(entityClass, list));
        }
    }

    public <Entity> List<Entity> findList(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                          Class<Entity> entityClass,
                                          Entity condition, boolean withConditionNull) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<List<ColumnInfo>> list = super.selectList(logger, transaction, entityInfo, condition, withConditionNull);

        List<Entity> result = new ArrayList<>();
        for (List<ColumnInfo> infoList : list) {
            result.add(entityResolver.resolve(entityClass, infoList));
        }
        return result;
    }

    public <Entity> List<Entity> findList(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                          Class<Entity> entityClass, String whereSql, Object... args) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<List<ColumnInfo>> list = super.selectList(logger, transaction, entityInfo, whereSql, args);

        List<Entity> result = new ArrayList<>();
        for (List<ColumnInfo> infoList : list) {
            result.add(entityResolver.resolve(entityClass, infoList));
        }
        return result;
    }

    public <Entity> List<Entity> findAll(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                             Class<Entity> entityClass) {
        EntityInfo<Entity> entityInfo = entityResolver.resolveEntityClass(entityClass);
        List<List<ColumnInfo>> lists = selectAll(logger, transaction, entityInfo);
        List<Entity> result = new ArrayList<>();
        for (List<ColumnInfo> infos : lists) {
            result.add(entityResolver.resolve(entityClass, infos));
        }
        return result;
    }

    public <E> Page<E> selectPaged(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                   EntityInfo<E> entityInfo,
                                   E condition, boolean withNull, PageRequest pageable) {
        DataSourceDriverName driverName = transaction.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, entityInfo, condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(logger, transaction, entityInfo, condition, withNull);
        return selectPaged(logger, transaction, entityResolver, entityInfo, pageable, sqlAndArgs, sql, count);
    }

    public <E> Page<E> selectPaged(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                   EntityInfo<E> entityInfo,
                                   PageRequest pageable, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, entityInfo, whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(logger, transaction, entityInfo);
        return selectPaged(logger, transaction, entityResolver, entityInfo, pageable, sqlAndArgs, sql, count);
    }

    private <E> Page<E> selectPaged(Logger logger, TransactionInfo transaction, EntityResolver entityResolver,
                                    EntityInfo<E> entityInfo, PageRequest pageable,
                                    SqlAndArgs<E> sqlAndArgs, String sql, Long count) {
        List<List<ColumnInfo>> content = super.query(logger, transaction, sql, sqlAndArgs.args);
        Class<E> javaType = entityInfo.getJavaType();
        List<E> result = new ArrayList<>();
        for (List<ColumnInfo> infos : content) {
            result.add(entityResolver.resolve(javaType, infos));
        }
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, result);
    }

    public <Id> boolean existsById(Logger logger, TransactionInfo transaction, String table, Id id) {
        return super.existsById(logger, transaction, table, "id", id);
    }
}
