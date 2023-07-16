package com.wiring.api.action;

import com.wiring.api.entity.Column;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;
import com.wiring.api.exception.WiringException;

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

            String sql = "CREATE TABLE IF NOT EXISTS " + database.getName() + "." + name + " ";

            StringBuilder sb = new StringBuilder();
            sb.append("(");
            int count = 1;
            for (Column column : columns) {
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
                if (column.getDefaultValue() != null) {
                    formatted = formatted + " DEFAULT '" + column.getDefaultValue().toString() + "'";
                }
                if (count == columns.size()) {
                    formatted = formatted;
                } else {
                    formatted = formatted + ", ";
                }

                sb.append(formatted);
                count++;
            }
            sb.append(");");

            sql = sql + sb.toString();

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();

            return new Table(name, database, connection);
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
