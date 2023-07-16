package com.wiring.api.action;

import com.wiring.api.WiringAPI;
import com.wiring.api.entity.Column;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;
import com.wiring.api.exception.WiringException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Insert {

    private WiringAPI api;

    private Database database;
    private Table table;

    private Map<Column, Object> info = new HashMap<Column, Object>();

    public Insert(Database database, WiringAPI api) {
        this.api = api;
        this.database = database;
    }

    public Insert table(Table table) {
        this.table = table;
        return this;
    }

    public Insert table(String table) {
        this.table = database.getTable(table);
        return this;
    }

    public Insert column(String column, Object data) {
        info.put(table.getColumn(column), data);
        return this;
    }

    public Insert column(Column column, Object data) {
        info.put(column, data);
        return this;
    }

    public void execute() {
        try {
            Statement s = api.getConnection().createStatement();

            String columns = "(";
            StringBuilder sb = new StringBuilder();
            int columnsCount = 1;
            for (Column column : info.keySet()) {
                if (columnsCount == info.size()) {
                    sb.append(column.getName());
                } else {
                    sb.append(column.getName() + ", ");
                }
                columnsCount++;
            }
            columns = columns + sb.toString() + ")";

            String values = " VALUES (";
            columnsCount = 1;
            sb = new StringBuilder();
            for (Column column : info.keySet()) {
                Object o = info.get(column);
                if (columnsCount == info.size()) {
                    sb.append("'" + o + "'");
                } else {
                    sb.append("'" + o + "', ");
                }
                columnsCount++;
            }
            values = values + sb.toString() + ")";

            String duplicate = " ON DUPLICATE KEY UPDATE ";
            columnsCount = 1;
            sb = new StringBuilder();
            for (Column column : info.keySet()) {
                Object o = info.get(column);
                if (columnsCount == info.size()) {
                    sb.append(column.getName() + " = '" + o + "'");
                } else {
                    sb.append(column.getName() + " = '" + o + "', ");
                }
                columnsCount++;
            }
            duplicate = duplicate + sb.toString() + ";";

            s.execute("USE " + database.getName() + ";");
            s.execute("INSERT INTO `" + table.getName() + "` " + columns + values + duplicate);

            s.close();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
