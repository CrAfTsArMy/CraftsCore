package de.craftsblock.craftscore.sql;

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
 * @version 1.0.1
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
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized void connect(String user, String password) throws SQLException {
        super.connect(user, password);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized void connect(String user, String password, boolean autoReconnect) throws SQLException {
        super.connect(user, password, autoReconnect);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized void disconnect() throws SQLException {
        super.disconnect();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized boolean isConnected() throws SQLException {
        return super.isConnected();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException {
        return super.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized int update(String sql) throws SQLException {
        return super.update(sql);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized int update(PreparedStatement statement) throws SQLException {
        return super.update(statement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized @NotNull ResultSet query(String query) throws SQLException {
        return super.query(query);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @NotNull
    @Override
    public synchronized ResultSet query(PreparedStatement statement) throws SQLException {
        return super.query(statement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public synchronized Connection unsafe() {
        return super.unsafe();
    }

}
