package com.wiring.api;

import com.wiring.api.action.DatabaseCreate;
import com.wiring.api.entity.Column;
import com.wiring.api.entity.ColumnType;
import com.wiring.api.entity.Database;
import com.wiring.api.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
            throw new RuntimeException(e);
        }

        WiringAPI api = new WiringAPI(host, username, password);

        api.createDatabase("test") // создание датабазы
                .execute() // выполнение запроса
                .createTable("table") // создание таблицы
                .column(new Column("name", ColumnType.VARCHAR).primaryKey().notNull()) // добавление колонны
                .column(new Column("age", ColumnType.INT).defaultValue(18)) // добавление колонны
                .column(new Column("info", ColumnType.LONGTEXT)) // добавление колонны
                .execute(); // выполнение запроса

    }

    public ResultSet execute(String sql) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                throw new DatabaseException("Такой базы возможно не существует");
            } catch (DatabaseException e) {
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
                throw new RuntimeException(e);
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