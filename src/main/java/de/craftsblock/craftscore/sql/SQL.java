package de.craftsblock.craftscore.sql;

import de.craftsblock.craftscore.actions.CompleteAbleAction;
import de.craftsblock.craftscore.actions.CompleteAbleActionImpl;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

/**
 * This class represents a simple wrapper for connecting to a MySQL database using JDBC.
 * It provides methods to establish and close connections, execute queries and updates,
 * and handle callback events for connection status changes.
 *
 * @author CraftsBlock
 * @version 1.3
 * @see ThreadSafeSQL
 * @see de.craftsblock.craftscore.sql.SQL.Callback
 * @since 3.6#15-SNAPSHOT
 */
public class SQL {

    private final Callback callback;

    private String host, database;
    private Integer port;
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

    /**
     * Binds the database connection parameters to be used when connecting to the MySQL database.
     *
     * @param host     The MySQL server host.
     * @param database The MySQL database name.
     */
    public void bind(String host, String database) {
        bind(host, 3306, database);
    }

    /**
     * Binds the database connection parameters to be used when connecting to the MySQL database.
     *
     * @param host     The MySQL server host.
     * @param port     The MySQL server port.
     * @param database The MySQL database name.
     */
    public void bind(String host, Integer port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    /**
     * Establishes a connection to the MySQL database using the provided credentials.
     *
     * @param user     The MySQL database username.
     * @param password The MySQL database password.
     * @throws SQLException if there is an error while connecting to the database.
     */
    public void connect(String user, String password) throws SQLException {
        if (host == null || port == null || database == null)
            throw new IllegalStateException("You have to bind your MySQL Connection! (Use \"bind(String, String)\" before connecting.)");
        if (isConnected())
            return;
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
        if (callback != null) callback.connect(this);
    }

    /**
     * Closes the existing connection to the MySQL database, if one exists.
     *
     * @throws SQLException if there is an error while disconnecting from the database.
     */
    public void disconnect() throws SQLException {
        if (!isConnected())
            return;
        if (connection != null) {
            connection.close();
            connection = null;
        }
        if (callback != null) callback.disconnect(this);
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
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
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
    public int update(String sql) throws SQLException {
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
    public int update(PreparedStatement statement) throws SQLException {
        if (statement.isClosed())
            throw new IllegalStateException("Is the statement already closed? If you are using a try-with-resources statement it is not necessary, because the statement is automatically closed after execution.");
        int result = statement.executeUpdate();
        statement.close();
        return result;
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
    public ResultSet query(PreparedStatement statement) throws SQLException {
        if (statement.isClosed())
            throw new IllegalStateException("Is the statement already closed? If you are using a try-with-resources statement it is not necessary, because the statement is automatically closed after execution.");
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
        void connect(SQL sql);

        /**
         * Invoked when the SQL connection is disconnected.
         *
         * @param sql The SQL instance that was disconnected.
         */
        void disconnect(SQL sql);

    }

}
