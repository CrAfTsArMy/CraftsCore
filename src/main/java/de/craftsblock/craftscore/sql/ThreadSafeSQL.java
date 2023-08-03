package de.craftsblock.craftscore.sql;

import de.craftsblock.craftscore.actions.CompleteAbleAction;
import de.craftsblock.craftscore.actions.CompleteAbleActionImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is a thread-safe subclass of the {@link SQL} class. It provides synchronized access
 * to all the public methods of the parent class, making it safe to be used in multi-threaded environments.
 *
 * @author CraftsBlock
 * @version 1.0
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
    public void connect(String user, String password) throws SQLException {
        synchronized (this) {
            super.connect(user, password);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public void disconnect() throws SQLException {
        synchronized (this) {
            super.disconnect();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public boolean isConnected() throws SQLException {
        synchronized (this) {
            return super.isConnected();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public CompleteAbleActionImpl<PreparedStatement> prepareStatement(String sql) {
        synchronized (this) {
            return super.prepareStatement(sql);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public CompleteAbleActionImpl<Integer> update(String sql) {
        synchronized (this) {
            return super.update(sql);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public CompleteAbleActionImpl<Integer> update(PreparedStatement statement) {
        synchronized (this) {
            return super.update(statement);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public CompleteAbleAction<ResultSet> query(String sql) {
        synchronized (this) {
            return super.query(sql);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public CompleteAbleAction<ResultSet> query(PreparedStatement statement) {
        synchronized (this) {
            return super.query(statement);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is synchronized to ensure thread-safe connection establishment.
     */
    @Override
    public Connection unsafe() {
        synchronized (this) {
            return super.unsafe();
        }
    }

}
