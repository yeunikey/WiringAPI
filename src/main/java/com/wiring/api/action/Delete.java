package com.wiring.api.action;

import com.wiring.api.WiringAPI;
import com.wiring.api.entity.Column;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;
import com.wiring.api.exception.WiringException;

import java.sql.SQLException;
import java.sql.Statement;

public class Delete {

    private WiringAPI api;

    private Database database;
    private Table table;

    private String key;
    private Object value;

    public Delete(Database database, WiringAPI api) {
        this.api = api;
        this.database = database;
    }

    public Delete table(Table table) {
        this.table = table;
        return this;
    }

    public Delete table(String table) {
        this.table = database.getTable(table);
        return this;
    }

    public Delete key(String key) {
        this.key = key;
        return this;
    }

    public Delete value(Object value) {
        this.value = value;
        return this;
    }

    public void execute() {
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
            statement.execute("DELETE FROM `" + table.getName() + "` WHERE " + table.getName() + "." + key + " = '" + value + "'");

            statement.close();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
