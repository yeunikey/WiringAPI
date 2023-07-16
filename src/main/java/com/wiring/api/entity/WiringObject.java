package com.wiring.api.entity;

import javax.swing.*;
import java.sql.Connection;

public class WiringObject implements Nameable, Dropable {

    private String name;
    private Connection connection;

    public WiringObject(String name, Connection connection) {
        this.name = name;
        this.connection = connection;

    }

    @Override
    public void drop() {

    }

    @Override
    public String getName() {
        return name;
    }

    public Connection getConnection() {
        return connection;
    }
}
