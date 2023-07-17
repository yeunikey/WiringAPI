package com.wiring.api.action;

import com.wiring.api.WiringAPI;
import com.wiring.api.entity.Column;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;
import com.wiring.api.entity.WiringResult;
import com.wiring.api.exception.WiringException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SelectAll {

    private WiringAPI api;

    private Database database;
    private Table table;

    public SelectAll(Database database, WiringAPI api) {
        this.api = api;
        this.database = database;
    }

    public SelectAll table(Table table) {
        this.table = table;
        return this;
    }

    public SelectAll table(String table) {
        this.table = database.getTable(table);
        return this;
    }

    public List<WiringResult> execute() {
        try {

            Statement statement = api.getConnection().createStatement();

            statement.execute("USE " + database.getName() + ";");
            statement.execute("SELECT * from `" + table.getName() + "`;");

            List<WiringResult> resultList = new ArrayList<WiringResult>();
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                WiringResult result = new WiringResult(table);
                for (Column column : table.getColumns()) {
                    result.write(column, resultSet.getObject(column.getName()));
                }
                resultList.add(result);
            }

            statement.close();

            return resultList;
        } catch (SQLException e) {
            try {
                throw new WiringException(e.getMessage());
            } catch (WiringException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
