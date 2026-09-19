package de.craftsblock.craftscore.sql;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.sql.*;

/**
 * This class represents a simple wrapper for connecting to a MySQL database using JDBC.
 * It provides methods to establish and close connections, execute queries and updates,
 * and handle callback events for connection status changes.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see de.craftsblock.craftscore.sql.SQL.Callback
 * @since 3.6#15-SNAPSHOT
 */
public class SQL {

    private final Callback callback;

    private @NotNull JdbcSubprotocol subprotocol = JdbcSubprotocol.MYSQL;
    private String host, database;
    private int port = 3306;
    private Connection connection;

    /**
     * Constructs a new SQL instance with no callback and automatic connection closing on shutdown.
     */
    public SQL() {
        this(null, true);
    }

    /**
     * Constructs a new SQL instance with the given callback and automatic connection closing on shutdown.
     *
     * @param callback The callback to be notified when the connection is established or closed.
     */
    public SQL(Callback callback) {
        this(callback, true);
    }

    /**
     * Constructs a new SQL instance with no callback and the option to disable automatic connection closing on shutdown.
     *
     * @param autoclose If true, the connection will be closed automatically on program shutdown.
     */
    public SQL(boolean autoclose) {
        this(null, autoclose);
    }

    /**
     * Constructs a new SQL instance with the given callback and the option to disable automatic connection closing on shutdown.
     *
     * @param callback  The callback to be notified when the connection is established or closed.
     * @param autoclose If true, the connection will be closed automatically on program shutdown.
     */
    public SQL(@Nullable Callback callback, boolean autoclose) {

        this.callback = callback;
        if (autoclose) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    disconnect();
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to close the sql connection on shutdown", e);
                }
            }));
        }
    }

    /**
     * Binds the database connection parameters to be used when connecting to the MySQL database.
     *
     * @param host     The MySQL server host.
     * @param database The MySQL database name.
     * @deprecated Use {@link #setHost(String)} and {@link #setDatabase(String)} instead
     */
    @Deprecated(since = "3.8.19", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "3.9.0")
    public void bind(String host, String database) {
        bind(host, 3306, database);
    }

    /**
     * Binds the database connection parameters to be used when connecting to the MySQL database.
     *
     * @param host     The MySQL server host.
     * @param port     The MySQL server port.
     * @param database The MySQL database name.
     * @deprecated Use {@link #setHost(String)}, {@link #setPort(int)} and {@link #setDatabase(String)} instead
     */
    @Deprecated(since = "3.8.19", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "3.9.0")
    public void bind(String host, int port, String database) {
        setHost(host);
        setPort(port);
        setDatabase(database);
    }

    /**
     * Sets the host to connect to.
     *
     * @param host The host to connect to.
     * @return The instance of {@link SQL} for chaining.
     */
    public SQL setHost(@NotNull String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the port to connect to.
     *
     * @param port The port to connect to.
     * @return The instance of {@link SQL} for chaining.
     */
    public SQL setPort(@Range(from = 0, to = Short.MAX_VALUE * 2) int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the database to connect to.
     *
     * @param database The database to connect to.
     * @return The instance of {@link SQL} for chaining.
     */
    public SQL setDatabase(@NotNull String database) {
        this.database = database;
        return this;
    }

    /**
     * Sets the subprotocol to use in the jdbc connect url.
     *
     * @param subprotocol The subprotocol.
     * @return The instance of {@link SQL} for chaining.
     */
    public SQL setSubprotocol(@NotNull JdbcSubprotocol subprotocol) {
        this.subprotocol = subprotocol;
        return this;
    }

    /**
     * Establishes a connection to the MySQL database using the provided credentials.
     *
     * @param user     The MySQL database username.
     * @param password The MySQL database password.
     * @throws SQLException if there is an error while connecting to the database.
     */
    public void connect(@NotNull String user, @NotNull String password) throws SQLException {
        connect(user, password, true);
    }

    /**
     * Establishes a connection to the MySQL database using the provided credentials and set the autoReconnect flag accordingly.
     *
     * @param user          The MySQL database username.
     * @param password      The MySQL database password.
     * @param autoReconnect Whether the connection should be automatically reconnected when the connection was lost.
     * @throws SQLException if there is an error while connecting to the database.
     */
    public void connect(@NotNull String user, @NotNull String password, boolean autoReconnect) throws SQLException {
        if (host == null) {
            throw new IllegalStateException("The host of the database is missing!");
        }

        if (database == null) {
            throw new IllegalStateException("The database name to connect to is missing!");
        }

        if (isConnected()) {
            return;
        }

        connection = DriverManager.getConnection(
                "jdbc:" + subprotocol + "://" + host + ":" + port + "/" + database + (autoReconnect ? "?autoReconnect=true" : "")
                , user, password
        );

        if (callback != null) {
            callback.connect(this);
        }
    }

    /**
     * Closes the existing connection to the MySQL database, if one exists.
     *
     * @throws SQLException if there is an error while disconnecting from the database.
     */
    public void disconnect() throws SQLException {
        if (!isConnected()) {
            return;
        }

        if (connection != null) {
            connection.close();
            connection = null;
        }

        if (callback != null) {
            callback.disconnect(this);
        }
    }

    /**
     * Checks if the sql instance is currently connected to the bound database.
     * If the current instance is not connected a {@link IllegalStateException}
     * will be thrown.
     *
     * @throws IllegalStateException if this instance is not connected to the
     *                               database.
     * @throws RuntimeException      if an {@link SQLException} occurs.
     * @since 3.8.5-SNAPSHOT
     */
    public void assertConnectedOrThrow() {
        try {
            if (isConnected()) {
                return;
            }

            throw new IllegalStateException("Not connected to the bound database!");
        } catch (SQLException e) {
            throw new RuntimeException("Could not check the sql connection status!", e);
        }
    }

    /**
     * Checks if there is an active connection to the MySQL database.
     *
     * @return True if a connection is active, false otherwise.
     * @throws SQLException If there is an error while checking the connection status.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement
     *                      or the SQL statement returns a ResultSet object
     */
    public boolean isConnected() throws SQLException {
        return connection != null && connection.isValid(5);
    }

    /**
     * Creates and returns a PreparedStatement for a given SQL query.
     *
     * @param sql The SQL query to be prepared.
     * @return The created PreparedStatement.
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     */
    public PreparedStatement prepareStatement(@NotNull String sql) throws SQLException {
        return this.prepareStatement(sql, false);
    }

    /**
     * Creates and returns a PreparedStatement for a given SQL query.
     * This method allows specifying whether the statement should return generated keys.
     *
     * @param sql        The SQL query to be prepared.
     * @param returnKeys If {@code true}, the statement will return generated keys, otherwise, it will not.
     * @return The created PreparedStatement.
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     */
    public PreparedStatement prepareStatement(@NotNull String sql, boolean returnKeys) throws SQLException {
        return connection.prepareStatement(sql, returnKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
    }

    /**
     * Creates and returns a new CompleteAbleAction for executing an update query.
     *
     * @param sql The SQL update query to be executed.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     * or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement
     *                      or the SQL statement returns a ResultSet object
     */
    public int update(@NotNull String sql) throws SQLException {
        return this.update(this.prepareStatement(sql));
    }

    /**
     * Creates and returns a new CompleteAbleAction for executing an update query using a prepared statement.
     *
     * @param statement The prepared statement containing the SQL update query to be executed.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     * or (2) 0 for SQL statements that return nothing
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement
     *                      or the SQL statement returns a ResultSet object
     */
    public int update(@NotNull PreparedStatement statement) throws SQLException {
        try (statement) {
            if (statement.isClosed()) {
                throw new IllegalStateException("The statement is already closed!");
            }

            return statement.executeUpdate();
        }
    }

    /**
     * Creates and returns a new CompleteAbleAction for executing a select query using a prepared statement.
     *
     * @param query The SQL query to be executed.
     * @return a {@link ResultSet} object that contains the data produced by the
     * query; never null
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  {@code PreparedStatement} or the SQL
     *                      statement does not return a {@code ResultSet} object
     */
    @NotNull
    public ResultSet query(@NotNull String query) throws SQLException {
        return this.query(this.prepareStatement(query));
    }

    /**
     * Creates and returns a new CompleteAbleAction for executing a select query using a prepared statement.
     *
     * @param statement The prepared statement containing the SQL select query to be executed.
     * @return a {@link ResultSet} object that contains the data produced by the
     * query; never null
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  {@code PreparedStatement} or the SQL
     *                      statement does not return a {@code ResultSet} object
     */
    @NotNull
    public ResultSet query(@NotNull PreparedStatement statement) throws SQLException {
        if (statement.isClosed()) {
            throw new IllegalStateException("The statement is already closed!");
        }

        return statement.executeQuery();
    }

    /**
     * Returns the underlying JDBC Connection object. Use with caution, as direct access might lead to unsafe operations.
     *
     * @return The underlying JDBC Connection object.
     */
    public Connection unsafe() {
        return connection;
    }

    /**
     * Gets the current callback associated with this SQL instance.
     *
     * @return The current callback instance or null if none is set.
     */
    public Callback callback() {
        return callback;
    }

    /**
     * A callback interface for handling connection events.
     * Implement this interface to receive notifications when the SQL connection is established or disconnected.
     */
    public interface Callback {

        /**
         * Invoked when the SQL connection is established.
         *
         * @param sql The SQL instance that was connected.
         */
        void connect(@NotNull SQL sql);

        /**
         * Invoked when the SQL connection is disconnected.
         *
         * @param sql The SQL instance that was disconnected.
         */
        void disconnect(@NotNull SQL sql);

    }

}
