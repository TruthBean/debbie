package com.truthbean.debbie.jdbc.entity;

import com.truthbean.debbie.jdbc.column.ColumnInfo;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/13 21:23.
 */
public class ResultMap<E> extends EntityInfo<E> {

    private final Supplier<E> initSupplier;

    public ResultMap(Supplier<E> initSupplier) {
        this.initSupplier = initSupplier;
    }

    public ResultMap(ResultMap<E> resultMap) {
        super(resultMap);
        this.initSupplier = resultMap.initSupplier;
    }

    public void addResult(Collection<ColumnInfo> columnInfos) {
        List<ColumnInfo> columnInfoList = super.getColumnInfoList();
        for (ColumnInfo columnInfo : columnInfoList) {
            for (ColumnInfo info : columnInfos) {
                if (columnInfo.getColumn().equals(info.getColumn())) {
                    columnInfo.setValue(info.getValue());
                }
            }
        }
    }

    public E toEntity() {
        E e = initSupplier.get();
        List<ColumnInfo> list = getColumnInfoList();
        for (ColumnInfo columnInfo : list) {
            columnInfo.getPropertySetter().set(e, columnInfo.getValue());
        }
        return e;
    }

    @Override
    public ResultMap<E> copy() {
        return new ResultMap<E>(this);
    }
}
