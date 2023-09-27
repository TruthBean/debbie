package com.truthbean.debbie.jdbc.mock;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class MockColumn {
    private int columnIndex;
    private String columnLabel;

    private Object data;
    private Class<?> dataType;

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public void setColumnLabel(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }
}
