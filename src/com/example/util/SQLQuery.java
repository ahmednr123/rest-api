package com.example.util;

import java.sql.*;

public class SQLQuery {
    public interface OnResult {
        void execute (ResultSet r) throws SQLException;
    }

    private Connection conn;
    private PreparedStatement statement;
    private ResultSet resultSet;

    private boolean getGeneratedKeys = false;

    public SQLQuery (String query) throws SQLException {
        conn = DatabaseManager.getInstance().getConnection();
        statement = conn.prepareStatement(query);
    }

    public SQLQuery (String query, boolean getGeneratedKeys) throws SQLException {
        conn = DatabaseManager.getInstance().getConnection();
        this.getGeneratedKeys = getGeneratedKeys;
        if (getGeneratedKeys) {
            statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } else {
            statement = conn.prepareStatement(query);
        }
    }

    public void setString (Integer paramIndex, String str) throws SQLException { statement.setString(paramIndex, str); }
    public void setInt (Integer paramIndex, Integer num) throws SQLException { statement.setInt(paramIndex, num); }

    public Integer execute () throws SQLException {
        return statement.executeUpdate();
    }

    public void execute (OnResult onResult) throws SQLException {
        if (getGeneratedKeys) {
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
        } else {
            resultSet = statement.executeQuery();
        }
        onResult.execute(resultSet);
    }

    public void close () {
        try { resultSet.close(); } catch (Exception e) {}
        try { statement.close(); } catch (Exception e) {}
        try { conn.close(); } catch (Exception e) {}
    }
}
