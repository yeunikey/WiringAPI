package com.wiring.api.entity;

public class Column {

    private String name;
    private ColumnType type;

    private Object defaultValue = null;
    private boolean isKey = false;
    private boolean isNull = true;

    public Column(String name, ColumnType type) {
        this.name = name;
        this.type = type;
    }

    public Column primaryKey() {
        isKey = true;
        return this;
    }

    public Column notNull() {
        isNull = false;
        return this;
    }

    public Column defaultValue(Object value) {
        defaultValue = value;
        return this;
    }

    public boolean isKey() {
        return isKey;
    }

    public boolean isNull() {
        return isNull;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Column setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Column setKey(boolean key) {
        isKey = key;
        return this;
    }

    public Column setNull(boolean aNull) {
        isNull = aNull;
        return this;
    }
}
