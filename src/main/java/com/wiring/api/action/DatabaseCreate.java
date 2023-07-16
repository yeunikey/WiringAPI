package com.wiring.api.action;

import com.wiring.api.WiringAPI;
import com.wiring.api.entity.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseCreate {

    private String name;
    private final Connection connection;

    public DatabaseCreate(String name, Connection connection) {
        this.name = name;
        this.connection = connection;
    }

    public Database execute() {
        try {
            PreparedStatement ps = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS '?'");
            ps.setString(1, name);
            ps.executeUpdate();

            return new Database(name, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
