package de.craftsblock.craftscore.sql;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is a thread-safe subclass of the {@link SQL} class. It provides synchronized access
 * to all the public methods of the parent class, making it safe to be used in multi-threaded environments.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @see SQL
 * @see de.craftsblock.craftscore.sql.SQL.Callback
 * @since 3.7.24-SNAPSHOT
 */
public class ThreadSafeSQL extends SQL {

    /**
     * Constructs a new SQL instance with no callback and automatic connection closing on shutdown.
     */
    public ThreadSafeSQL() {
        this(null, true);
    }

    /**
     * Constructs a new SQL instance with the given callback and automatic connection closing on shutdown.
     *
     * @param callback The callback to be notified when the connection is established or closed.
     */
    public ThreadSafeSQL(Callback callback) {
        this(callback, true);
    }

    /**
     * Constructs a new SQL instance with no callback and the option to disable automatic connection closing on shutdown.
     *
     * @param autoclose If true, the connection will be closed automatically on program shutdown.
     */
    public ThreadSafeSQL(boolean autoclose) {
        this(null, autoclose);
    }

    /**
     * Constructs a new SQL instance with the given callback and the option to disable automatic connection closing on shutdown.
     *
     * @param callback  The callback to be notified when the connection is established or closed.
     * @param autoclose If true, the connection will be closed automatically on program shutdown.
     */
    public ThreadSafeSQL(Callback callback, boolean autoclose) {
        super(callback, autoclose);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     *
     * @deprecated Use {@link #setHost(String)} and {@link #setDatabase(String)} instead
     */
    @Override
    @SuppressWarnings("removal")
    @Deprecated(since = "3.8.19", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "3.9.0")
    public synchronized void bind(String host, String database) {
        super.bind(host, database);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     *
     * @deprecated Use {@link #setHost(String)}, {@link #setPort(int)} and {@link #setDatabase(String)} instead
     */
    @Override
    @SuppressWarnings("removal")
    @Deprecated(since = "3.8.19", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "3.9.0")
    public synchronized void bind(String host, int port, String database) {
        super.bind(host, port, database);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized SQL setHost(@NotNull String host) {
        return super.setHost(host);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized SQL setPort(int port) {
        return super.setPort(port);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized SQL setDatabase(@NotNull String database) {
        return super.setDatabase(database);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized SQL setSubprotocol(@NotNull JdbcSubprotocol subprotocol) {
        return super.setSubprotocol(subprotocol);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized void connect(@NotNull String user, @NotNull String password) throws SQLException {
        super.connect(user, password);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized void connect(@NotNull String user, @NotNull String password, boolean autoReconnect) throws SQLException {
        super.connect(user, password, autoReconnect);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized void disconnect() throws SQLException {
        super.disconnect();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized boolean isConnected() throws SQLException {
        return super.isConnected();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized PreparedStatement prepareStatement(@NotNull String sql) throws SQLException {
        return super.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized int update(@NotNull String sql) throws SQLException {
        return super.update(sql);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized int update(@NotNull PreparedStatement statement) throws SQLException {
        return super.update(statement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized @NotNull ResultSet query(@NotNull String query) throws SQLException {
        return super.query(query);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @NotNull
    @Override
    public synchronized ResultSet query(@NotNull PreparedStatement statement) throws SQLException {
        return super.query(statement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized.
     */
    @Override
    public synchronized Connection unsafe() {
        return super.unsafe();
    }

}
