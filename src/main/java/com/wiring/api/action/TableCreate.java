package com.wiring.api.action;

import com.wiring.api.entity.Column;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableCreate {

    private String name;
    private Database database;
    private Connection connection;

    private List<Column> columns = new ArrayList<Column>();

    public TableCreate(String name, Database database, Connection connection) {
        this.name = name;
        this.database = database;
        this.connection = connection;
    }

    public TableCreate column(Column column) {
        columns.add(column);
        return this;
    }

    public Table execute() {
        try {

            String sql = "CREATE TABLE IF NOT EXISTS `?` ";

            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (Column column : columns) {
                String formatted = column.getName();
                if (column.isKey()) {
                    formatted = formatted + " PRIMARY KEY";
                }
                if (column.isNull()) {
                    formatted = formatted + " NOT NULL";
                }
                if (column.getDefaultValue() != null) {
                    formatted = formatted + " DEFAULT `" + column.getDefaultValue() + "`";
                }
            }
            sb.append(")");

            sql = sql + sb.toString();

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.executeUpdate();

            return new Table(name, database, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
