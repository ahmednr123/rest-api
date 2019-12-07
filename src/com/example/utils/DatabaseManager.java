package com.example.utils;

import java.util.logging.Logger;

import java.sql.SQLException;
import java.sql.Connection;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * @author Ahmed Noor
 *
 * Establish a connection pool to MySQL database
 * and return connections from the connection pool
 */
public class DatabaseManager {
    private static DatabaseManager instance;

    private DataSource dataSource;
    private static Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/rest_app";

    //  Database credentials
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    /**
     * Blocking Object Creation
     */
    private DatabaseManager () {
        PoolProperties poolProps = new PoolProperties();
        poolProps.setUrl(DB_URL);
        poolProps.setDriverClassName(JDBC_DRIVER);
        poolProps.setUsername(DB_USER);
        poolProps.setPassword(DB_PASS);

        dataSource = new DataSource();
        dataSource.setPoolProperties(poolProps);

        LOGGER.info("DatabaseManager Initialized");
    }

    /**
     * Initialize DataSource to get connections
     * from Connection Pool
     */
    static {
        instance = new DatabaseManager();
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    /**
     * @return DB Connection
     * @exception SQLException
     *					If a database access error occurs
     */
    public
    Connection getConnection()
            throws SQLException
    {
        LOGGER.info("DBConnection Request");
        return dataSource.getConnection();
    }
}