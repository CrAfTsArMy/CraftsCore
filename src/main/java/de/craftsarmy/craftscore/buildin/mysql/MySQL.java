package de.craftsarmy.craftscore.buildin.mysql;

import de.craftsarmy.craftscore.api.mysql.AbstractMySQL;

import java.sql.*;

public final class MySQL extends AbstractMySQL {

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
            try {
                DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true",
                        user,
                        password
                );
                if (getCallback() != null)
                    getCallback().connect(this);
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
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
}
