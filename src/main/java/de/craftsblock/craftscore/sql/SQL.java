package de.craftsblock.craftscore.sql;

import de.craftsblock.craftscore.actions.CompleteAbleAction;
import de.craftsblock.craftscore.actions.CompleteAbleActionImpl;

import java.sql.*;

public final class SQL {

    private final Callback callback;

    private String host, database;
    private Integer port;
    private Connection connection;

    public SQL() {
        this(null, true);
    }

    public SQL(Callback callback) {
        this(callback, true);
    }

    public SQL(boolean autoclose) {
        this(null, autoclose);
    }

    public SQL(Callback callback, boolean autoclose) {
        this.callback = callback;
        if (autoclose)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    disconnect();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }));
    }

    public void bind(String host, String database) {
        bind(host, 3306, database);
    }

    public void bind(String host, Integer port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public void connect(String user, String password) throws SQLException {
        if (host == null || port == null || database == null)
            throw new IllegalStateException("You have to bind your MySQL Connection! (Use \"bind(String, String)\" before connecting.)");
        if (isConnected())
            return;
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
        if (callback != null) callback.connect(this);
    }

    public void disconnect() throws SQLException {
        if (!isConnected())
            return;
        if (connection != null) {
            connection.close();
            connection = null;
        }
        if (callback != null) callback.disconnect(this);
    }

    public boolean isConnected() throws SQLException {
        return connection != null && connection.isValid(5);
    }

    public CompleteAbleActionImpl<PreparedStatement> prepareStatement(String sql) {
        return new CompleteAbleActionImpl<>(() -> connection.prepareStatement(sql));
    }

    public CompleteAbleActionImpl<Integer> update(String sql) {
        return new CompleteAbleActionImpl<>(() -> this.update(this.prepareStatement(sql).complete()).complete());
    }

    public CompleteAbleActionImpl<Integer> update(PreparedStatement statement) {
        return new CompleteAbleActionImpl<>(() -> {
            if (statement.isClosed())
                throw new IllegalStateException("Is the statement already closed? If you are using a try-with-resources statement it is not necessary, because the statement is automatically closed after execution.");
            int result = statement.executeUpdate();
            statement.close();
            return result;
        });
    }

    public CompleteAbleAction<ResultSet> query(String sql) {
        return new CompleteAbleActionImpl<>(() -> this.query(this.prepareStatement(sql).complete()).complete());
    }

    public CompleteAbleAction<ResultSet> query(PreparedStatement statement) {
        return new CompleteAbleActionImpl<>(() -> {
            if (statement.isClosed())
                throw new IllegalStateException("Is the statement already closed? If you are using a try-with-resources statement it is not necessary, because the statement is automatically closed after execution.");
            ResultSet result = statement.executeQuery();
            statement.close();
            return result;
        });
    }

    public Connection unsafe() {
        return connection;
    }

    public Callback callback() {
        return callback;
    }

    public interface Callback {
        void connect(SQL sql);

        void disconnect(SQL sql);
    }

}
