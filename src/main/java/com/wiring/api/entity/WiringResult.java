package com.wiring.api.entity;

import com.wiring.api.WiringAPI;

import java.util.HashMap;
import java.util.Map;

public class WiringResult {

    private Table table;

    private Map<Column, Object> result = new HashMap<Column, Object>();

    public WiringResult(Table table) {
        this.table = table;
    }

    public Map<String, Object> formatted() {
        Map<String, Object> map = new HashMap<String, Object>();
        result.forEach((column, o) -> {
            map.put(column.getName(), o);
        });
        return map;
    }

    public Object get(String column) {
        Column c = null;
        for (Column col : result.keySet()) {
            if (col.getName().equals(column)) {
                c = col;
            }
        }
        return result.get(c);
    }

    public WiringResult write(Column column, Object value) {
        result.put(column, value);
        return this;
    }

    public WiringResult write(String column, Object value) {
        result.put(table.getColumn(column), value);
        return this;
    }

    public Table getTable() {
        return table;
    }

    public Map<Column, Object> getResult() {
        return result;
    }

}
