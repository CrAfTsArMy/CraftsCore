package de.craftsblock.craftscore.sql;

/**
 * This class represents a JDBC subprotocol used to identify the JDBC driver
 * that should be used when establishing a database connection.
 * It provides predefined subprotocols for commonly used database systems
 * while also allowing custom subprotocols to be created.
 *
 * @author Philipp Maywald
 * @author CraftsBlock
 * @since 3.8.19
 */
public final class JdbcSubprotocol {

    /**
     * The JDBC subprotocol used for PostgreSQL database connections.
     */
    public static final JdbcSubprotocol POSTGRESQL = of("postgresql");

    /**
     * The JDBC subprotocol used for MySQL database connections.
     */
    public static final JdbcSubprotocol MYSQL = of("mysql");

    /**
     * The JDBC subprotocol used for MariaDB database connections.
     */
    public static final JdbcSubprotocol MARIADB = of("mariadb");

    /**
     * The JDBC subprotocol used for Oracle database connections.
     */
    public static final JdbcSubprotocol ORACLE = of("oracle");

    /**
     * The JDBC subprotocol used for Microsoft SQL Server database connections.
     */
    public static final JdbcSubprotocol SQLSERVER = of("sqlserver");

    private final String value;

    /**
     * Constructs a new JdbcSubprotocol with the given value.
     *
     * @param value The value of the JDBC subprotocol.
     * @throws IllegalArgumentException if the value is null, blank, or does not
     *                                  represent a valid JDBC subprotocol.
     */
    public JdbcSubprotocol(String value) {
        this.value = value;

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Subprotocol must not be blank");
        }

        if (!value.matches("[A-Za-z][A-Za-z0-9+.-]*")) {
            throw new IllegalArgumentException(
                    "Invalid JDBC subprotocol: " + value
            );
        }
    }

    /**
     * Creates a new JdbcSubprotocol with the given value.
     * This method can be used to create custom JDBC subprotocols
     * that are not provided as predefined constants.
     *
     * @param value The value of the JDBC subprotocol.
     * @return A new {@link JdbcSubprotocol} instance.
     * @throws IllegalArgumentException if the value is null, blank, or does not
     *                                  represent a valid JDBC subprotocol.
     */
    public static JdbcSubprotocol of(String value) {
        return new JdbcSubprotocol(value);
    }

    /**
     * Returns the value of this JDBC subprotocol.
     *
     * @return The JDBC subprotocol value.
     */
    @Override
    public String toString() {
        return value;
    }

}