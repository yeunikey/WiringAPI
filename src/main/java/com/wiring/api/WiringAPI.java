package com.wiring.api;

import com.wiring.api.action.*;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.WiringResult;
import com.wiring.api.exception.WiringException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class WiringAPI {

    private HikariConfig config = new HikariConfig();
    private HikariDataSource dataSource;

    private Connection connection;

    public WiringAPI(String host, String username, String password){
        this(host, 3306, username, password, null);
    }

    public WiringAPI(String host, String username, String password, Map<String, String> properties){
        this(host, 3306, username, password, properties);
    }

    public WiringAPI(String host, int port, String username, String password, Map<String, String> properties) {
        this("com.mysql.cj.jdbc.Driver", host, port, username, password, properties);
    }

    public WiringAPI(String driver, String host, int port, String username, String password, Map<String, String> properties) {
        config.setDriverClassName(driver);
        config.setJdbcUrl( "jdbc:mysql://" + host +":" + port);
        config.setUsername(username);
        config.setPassword(password);

        if (properties != null) {
            for (String property : properties.keySet()) {
                config.addDataSourceProperty(property, properties.get(property));
            }
        }

        dataSource = new HikariDataSource( config );
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public Insert insert(String name) {
        return new Insert(getDatabase(name), this);
    }

    public Insert insert(Database database) {
        return new Insert(database, this);
    }

    public Select select(String name) {
        return new Select(getDatabase(name), this);
    }

    public Select select(Database database) {
        return new Select(database, this);
    }

    public SelectAll selectAll(String name) {
        return new SelectAll(getDatabase(name), this);
    }

    public SelectAll selectAll(Database database) {
        return new SelectAll(database, this);
    }

    public Delete delete(String name) {
        return new Delete(getDatabase(name), this);
    }

    public Delete delete(Database database) {
        return new Delete(database, this);
    }

    public ResultSet execute(String sql) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public DatabaseCreate createDatabase(String name) {
        return new DatabaseCreate(name, connection);
    }

    public boolean existsDatabase(String name) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE DATABASE " + name + ";");
            statement.execute("DROP DATABASE " + name + ";");
            closeStatement(statement);
            return false;
        } catch (SQLException e) {
            return true;
        }
    }

    public Database getDatabase(String name) {
        if (existsDatabase(name)) {
            return new Database(name, connection);
        } else {
            try {
                throw new WiringException("Такой базы возможно не существует");
            } catch (WiringException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        try {
            getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeStatement(Statement statement) {
        if (statement != null) {
            try {
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

    public HikariConfig getConfig() {
        return config;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() {
        return connection;
    }
}