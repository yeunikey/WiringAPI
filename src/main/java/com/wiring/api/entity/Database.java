package com.wiring.api.entity;

import com.wiring.api.action.TableCreate;
import com.wiring.api.exception.DatabaseException;
import com.wiring.api.exception.TableException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database extends WiringObject {

    public Database(String name, Connection connection) {
        super(name, connection);
    }

    public TableCreate createTable(String name) {
        return new TableCreate(name, this, getConnection());
    }

    public boolean existsTable(String name) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT `?` FROM `?`");
            ps.setString(1, name);
            ps.setString(2, getName());
            ps.executeQuery();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Table getTable(String name) {
        if (existsTable(name)) {
            return new Table(name, this, getConnection());
        } else {
            try {
                throw new TableException("Такой таблицы возможно не существует");
            } catch (TableException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void drop() {
        super.drop();

        try {
            PreparedStatement ps = getConnection().prepareStatement("DROP DATABASE `?`;");
            ps.setString(1, getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
