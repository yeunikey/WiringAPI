package com.wiring.api.action;

import com.wiring.api.WiringAPI;
import com.wiring.api.entity.Column;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;
import com.wiring.api.entity.WiringResult;
import com.wiring.api.exception.WiringException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Select {

    private WiringAPI api;

    private Database database;
    private Table table;

    private String key;
    private Object value;

    public Select(Database database, WiringAPI api) {
        this.api = api;
        this.database = database;
    }

    public Select table(Table table) {
        this.table = table;
        return this;
    }

    public Select table(String table) {
        this.table = database.getTable(table);
        return this;
    }

    public Select key(String key) {
        this.key = key;
        return this;
    }

    public Select value(Object value) {
        this.value = value;
        return this;
    }

    public WiringResult execute() {
        try {

            if (key == null) {
                Column column = null;
                for (Column col : table.getColumns()) {
                    if (col.isKey()) {
                        key = col.getName();
                    }
                }
            }

            Statement statement = api.getConnection().createStatement();

            statement.execute("USE " + database.getName() + ";");
            statement.execute("SELECT * from `" + table.getName() + "` WHERE " + key + " = '" + value + "';");

            WiringResult result = new WiringResult(table);
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                for (Column column : table.getColumns()) {
                    result.write(column, resultSet.getObject(column.getName()));
                }
            }

            statement.close();

            return result;
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


}
