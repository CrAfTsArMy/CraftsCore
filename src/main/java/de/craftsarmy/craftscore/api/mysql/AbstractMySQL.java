package de.craftsarmy.craftscore.api.mysql;

import de.craftsarmy.craftscore.api.config.AbstractConfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class AbstractMySQL {

    private MySQLCallback callback;

    public abstract AbstractMySQL bind(String host, String database);
    public abstract AbstractMySQL bind(String host, Integer port, String database);

    public abstract AbstractMySQL connect(String user, String password);
    public abstract AbstractMySQL disconnect();
    public abstract boolean isConnected();

    public abstract PreparedStatement prepareStatement(String sql);

    public final AbstractMySQL setCallback(MySQLCallback callback) {
        this.callback = callback;
        return this;
    }

    public final MySQLCallback getCallback() {
        return callback;
    }

    public abstract AbstractMySQL update(String sql);
    public abstract AbstractMySQL update(PreparedStatement statement);

    public abstract ResultSet query(String sql);
    public abstract ResultSet query(PreparedStatement statement);

}
