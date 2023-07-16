package com.wiring.api.entity;

import com.wiring.api.exception.WiringException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Table extends WiringObject implements Dropable {

    private Database database;

    public Table(String name, Database database, Connection connection) {
        super(name, connection);
        this.database = database;
    }

    public boolean existsColumn(String name) {
        if (getColumn(name) == null) {
            return false;
        }
        return true;
    }

    public Column getColumn(String name) {
        for (Column column : getColumns()) {
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }

    public Table renameTable(String name) {
        try {
            Statement ps = getConnection().createStatement();
            ps.execute("USE " + database.getName() + ";");
            ps.execute("ALTER TABLE " + getName() + " RENAME " + name + ";");
            ps.close();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
        return database.getTable(name);
    }

    public Table createColumn(Column column) {
        try {
            String type = column.getType().toString();
            if (type.equalsIgnoreCase("VARCHAR")) {
                type = "VARCHAR(255)";
            }
            String formatted = column.getName() + " " + type;
            if (column.isKey()) {
                formatted = formatted + " PRIMARY KEY";
            }
            if (!column.isNull()) {
                formatted = formatted + " NOT NULL";
            }

            PreparedStatement ps = getConnection().prepareStatement("ALTER TABLE " + database.getName() + "." + getName() + " ADD " + formatted + ";");
            ps.executeUpdate();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    public Table dropColumn(String name) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("ALTER TABLE " + database.getName() + "." + getName() + " DROP COLUMN " + name + ";");
            ps.executeUpdate();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<Column>();

        try {
            PreparedStatement ps = getConnection().prepareStatement("DESCRIBE " + database.getName() + "." + getName() + ";");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                columns.add(
                        new Column(
                                result.getString("Field"),
                                result.getString("Type").toUpperCase().equals("VARCHAR(255)") ? ColumnType.VARCHAR : ColumnType.valueOf(result.getString("Type").toUpperCase()))
                                .setNull(result.getString("Null").equals("YES") ? true : false)
                                .setKey(result.getString("Key").equals("PRI") ? true : false)
                                .setDefaultValue(result.getObject("Default")));
            }
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }

        return columns;
    }

    public Table setName(String name) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("ALTER TABLE " + getName() + " RENAME TO " + name + ";");
            ps.executeUpdate();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }

        return database.getTable(name);
    }

    @Override
    public void drop() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("DROP TABLE " + database.getName() + "." + getName() + ";");
            ps.executeUpdate();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public Database getDatabase() {
        return database;
    }

}
