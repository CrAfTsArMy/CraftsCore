package de.craftsblock.craftscore.api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractMySQL {

    private MySQLCallback callback;

    public abstract AbstractMySQL bind(String host, String database);

    public abstract AbstractMySQL bind(String host, Integer port, String database);

    public abstract AbstractMySQL connect(String user, String password) throws SQLException;

    public abstract AbstractMySQL disconnect() throws SQLException;

    public abstract boolean isConnected() throws SQLException;

    public abstract PreparedStatement prepareStatement(String sql) throws SQLException;

    public final AbstractMySQL setCallback(MySQLCallback callback) {
        this.callback = callback;
        return this;
    }

    public final MySQLCallback getCallback() {
        return callback;
    }

    public abstract AbstractMySQL update(String sql) throws SQLException;

    public abstract AbstractMySQL update(PreparedStatement statement) throws SQLException;

    public abstract ResultSet query(String sql) throws SQLException;

    public abstract ResultSet query(PreparedStatement statement) throws SQLException;

}
