package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import com.truthbean.code.debbie.jdbc.column.ColumnInfo;
import com.truthbean.code.debbie.jdbc.transaction.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:23.
 */
public class DmlRepositoryHandler<E, ID> extends RepositoryHandler {

    public DmlRepositoryHandler(Connection connection) {
        super(connection);
    }

    private EntityInfo<E> getEntityInfo() {
        return EntityResolver.resolveEntityClass(getEntityClass());
    }

    private Class<E> getEntityClass() {
        try {
            var types = ReflectionHelper.getActualTypes(getClass());
            if (types != null && types.length > 0) {
                return (Class<E>) types[0];
            }
        } catch (Exception e) {
            LOGGER.error("getActualTypeArguments error. ", e);
        }
        throw new RuntimeException("get entity class error");
    }

    private static class ConditionAndValue {
        private String conditionSql;
        private List<Object> conditionValues;
    }

    public int deleteById(ID id)  throws TransactionException {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        String sql = DynamicSqlBuilder.sql().delete().from(table).where().eq(primaryKey.getColumnName(), "?").builder();
        return update(sql, id);
    }

    private ConditionAndValue resolveCondition(EntityInfo<E> entityInfo) {
        List<ColumnInfo> columns = entityInfo.getColumnInfoList();

        var sqlBuilder = DynamicSqlBuilder.sql().eq("1", "1");
        List<Object> columnValues = new LinkedList<>();

        for (ColumnInfo column : columns) {
            var value = column.getValue();
            if (value != null) {
                columnValues.add(column.getValue());
                sqlBuilder.and(column.getColumnName() + " = ?");
            }
        }

        var conditionAndValue = new ConditionAndValue();
        conditionAndValue.conditionSql = sqlBuilder.builder();
        conditionAndValue.conditionValues = columnValues;
        return conditionAndValue;
    }

    public int delete(E condition) throws TransactionException {
        var entityInfo = EntityResolver.resolveEntity(condition);
        var table = entityInfo.getTable();
        var conditionAndValue = resolveCondition(entityInfo);

        var sqlBuilder = DynamicSqlBuilder.sql().delete().from(table).where().extra(conditionAndValue.conditionSql);

        List<Object> columnValues = conditionAndValue.conditionValues;

        var sql = sqlBuilder.builder();
        LOGGER.debug(sql);
        return update(sql, columnValues.toArray());
    }

    public ID insert(E entity) throws TransactionException {
        var entityInfo = EntityResolver.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        List<Object> columnValues = new LinkedList<>();
        List<Object> signs = new LinkedList<>();
        for (ColumnInfo column : columns) {
            var value = column.getValue();
            if (value != null) {
                columnValues.add(column.getValue());
                columnNames.add(column.getColumnName());
                signs.add("?");
            }
        }

        var sql = DynamicSqlBuilder.sql().insert().extra(table).leftParenthesis()
                .columns(columnNames).rightParenthesis().values(signs).builder();
        LOGGER.debug(sql);
        var primaryKey = entityInfo.getPrimaryKey();
        var generatedKeys = primaryKey.getPrimaryKeyType() != null;
        return (ID) super.insert(sql, generatedKeys, primaryKey.getJavaClass(), columnValues.toArray());
    }

    public boolean update(E entity) throws TransactionException {
        var entityInfo = EntityResolver.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        List<Object> columnValues = new LinkedList<>();
        for (ColumnInfo column : columns) {
            var value = column.getValue();
            if (!column.isPrimaryKey() && value != null) {
                columnValues.add(column.getValue());
                columnNames.add(column.getColumnName());
            }
        }

        columnValues.add(primaryKey.getValue());

        var sql = DynamicSqlBuilder.sql().update(table).set(columnNames)
                .where().eq(primaryKey.getColumnName(), "?").builder();
        LOGGER.debug(sql);
        return super.update(sql, columnValues.toArray()) == 1;
    }

    public E findOne(E condition) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var conditionInfo = EntityResolver.resolveEntity(condition);
        var conditionAndValues = resolveCondition(conditionInfo);
        var sql = DynamicSqlBuilder.sql().select(columnNames).from(table)
                .where().extra(conditionAndValues.conditionSql).builder();
        LOGGER.debug(sql);
        return super.selectOne(sql, entityClass, conditionAndValues.conditionValues.toArray());
    }

    public List<E> findList(E condition) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sqlBuilder = DynamicSqlBuilder.sql().select(columnNames).from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionInfo = EntityResolver.resolveEntity(condition);
            var conditionAndValues = resolveCondition(conditionInfo);
            sqlBuilder.where().extra(conditionAndValues.conditionSql);
            args = conditionAndValues.conditionValues.toArray();
        }

        var sql = sqlBuilder.builder();
        LOGGER.debug(sql);
        return super.select(sql, entityClass, args);
    }

    public E findById(ID id) {
        var entityClass = getEntityClass();
        var entityInfo = EntityResolver.resolveEntityClass(entityClass);

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql().select(columnNames).from(table)
                .where().eq(primaryKey.getColumnName(), "?").builder();
        LOGGER.debug(">>>>>>>>>>>> " + sql);
        LOGGER.debug(">>>>>>>>>>>>> " + id);
        return super.selectOne(sql, entityClass, id);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DmlRepositoryHandler.class);
}
