package com.myorg.commentai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private final String url;

    public DatabaseHelper(String dbFilePath) {
        this.url = "jdbc:sqlite:" + dbFilePath;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "text TEXT NOT NULL," +
                "sentiment TEXT," +
                "response TEXT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRecord(String text, String sentiment, String response) {
        String sql = "INSERT INTO comments(text, sentiment, response) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.setString(2, sentiment);
            pstmt.setString(3, response);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllRecords() {
        List<String> rows = new ArrayList<>();
        String sql = "SELECT id, text, sentiment, response, created_at FROM comments ORDER BY created_at DESC";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String row = String.format("[%d] (%s) %s => %s",
                        rs.getInt("id"), rs.getString("created_at"),
                        rs.getString("text"), rs.getString("response"));
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
