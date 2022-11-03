package de.craftsblock.craftscore.buildin.mysql;

import de.craftsblock.craftscore.api.mysql.AbstractMySQL;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MySQL extends AbstractMySQL {

    private static final ConcurrentHashMap<String, Connection> cache = new ConcurrentHashMap<>();

    private boolean bind = false;
    private String host, database;
    private Integer port;

    private Connection connection;

    @Override
    public AbstractMySQL bind(String host, String database) {
        bind(host, 3306, database);
        return this;
    }

    @Override
    public AbstractMySQL bind(String host, Integer port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.bind = true;
        return this;
    }

    @Override
    public AbstractMySQL connect(String user, String password) {
        if (!bind)
            throw new IllegalStateException("You have to bind your MySQL Connection! (Use \"bind(String, String)\" before connecting.)");
        if (!isConnected()) {
            if (cache.containsKey(host + ":" + port + ":" + database)) {
                connection = cache.get(host + ":" + port + ":" + database);
                if (getCallback() != null) getCallback().connect(this);
                return this;
            }
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
                cache.put(host + ":" + port + ":" + database, connection);
                if (getCallback() != null) getCallback().connect(this);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public AbstractMySQL disconnect() {
        try {
            connection.close();
            connection = null;
            if (getCallback() != null)
                getCallback().disconnect(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && connection.isValid(5);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AbstractMySQL update(String sql) {
        if (isConnected()) {
            try {
                connection.createStatement().executeLargeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("You have to connect to a Database before requesting!");
        }
        return this;
    }

    @Override
    public AbstractMySQL update(PreparedStatement statement) {
        if (isConnected())
            try {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return this;
    }

    @Override
    public ResultSet query(String sql) {
        if (isConnected()) {
            try {
                return connection.createStatement().executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        } else
            throw new IllegalStateException("You have to connect to a Database before requesting!");
    }

    @Override
    public ResultSet query(PreparedStatement statement) {
        if (isConnected())
            try {
                return statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return null;
    }

}
