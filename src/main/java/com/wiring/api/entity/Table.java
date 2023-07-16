package com.wiring.api.entity;

import com.mysql.cj.xdevapi.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Table extends WiringObject implements Dropable {

    private Database database;

    public Table(String name, Database database, Connection connection) {
        super(name, connection);
        this.database = database;
    }

    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<Column>();

        try {
            PreparedStatement ps = getConnection().prepareStatement("DESCRIBE `?.?`");
            ps.setString(1, database.getName());
            ps.setString(2, getName());
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                columns.add(
                        new Column(
                                result.getString("Field"),
                                ColumnType.valueOf(result.getString("Type").toUpperCase()))
                                .setNull(result.getString("Null").equals("YES") ? true : false)
                                .setKey(result.getString("Key").equals("PRI") ? true : false)
                                .setDefaultValue(result.getObject("Default")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return columns;
    }

    public Table setName(String name) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("ALTER TABLE `?` RENAME TO `?`;");
            ps.setString(1, getName());
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return database.getTable(name);
    }

    @Override
    public void drop() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("DROP TABLE `?.?`");
            ps.setString(1, database.getName());
            ps.setString(2, getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Database getDatabase() {
        return database;
    }

}
