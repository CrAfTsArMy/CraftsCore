package de.craftsblock.craftscore.buildin.mysql;

import de.craftsblock.craftscore.api.mysql.AbstractMySQL;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MySQL extends AbstractMySQL {

    private static final ConcurrentHashMap<String, Connection> cache = new ConcurrentHashMap<>();

    private boolean bind = false;
    private String host, database;
    private Integer port;

    private String local;
    private Connection connection;

    @Override
    public MySQL bind(String host, String database) {
        bind(host, 3306, database);
        return this;
    }

    @Override
    public MySQL bind(String host, Integer port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.bind = true;
        return this;
    }

    @Override
    public MySQL connect(String user, String password) throws SQLException {
        if (!bind)
            throw new IllegalStateException("You have to bind your MySQL Connection! (Use \"bind(String, String)\" before connecting.)");
        if (!isConnected()) {
            local = host + ":" + port + ":" + database;
            if (cache.containsKey(local)) {
                connection = cache.get(local);
                if (getCallback() != null) getCallback().connect(this);
                return this;
            }

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            cache.put(host + ":" + port + ":" + database, connection);
            if (getCallback() != null) getCallback().connect(this);
        }
        return this;
    }

    @Override
    public MySQL disconnect() throws SQLException {
        cache.remove(local);
        connection.close();
        connection = null;
        if (getCallback() != null)
            getCallback().disconnect(this);
        return this;
    }

    @Override
    public boolean isConnected() throws SQLException {
        return connection != null && connection.isValid(5);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public MySQL update(String sql) throws SQLException {
        if (isConnected()) {
            connection.createStatement().executeLargeUpdate(sql);
        } else {
            throw new IllegalStateException("You have to connect to a Database before requesting!");
        }
        return this;
    }

    @Override
    public MySQL update(PreparedStatement statement) throws SQLException {
        if (isConnected())
            statement.executeUpdate();
        return this;
    }

    @Override
    public ResultSet query(String sql) throws SQLException {
        if (isConnected()) {
            return connection.createStatement().executeQuery(sql);
        } else
            throw new IllegalStateException("You have to connect to a Database before requesting!");
    }

    @Override
    public ResultSet query(PreparedStatement statement) throws SQLException {
        if (isConnected())
            return statement.executeQuery();
        return null;
    }

}
