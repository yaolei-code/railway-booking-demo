package org.example.studentcoursesystem.dao;

import org.example.studentcoursesystem.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class SemesterDAO {
    public List<Map<String, String>> getAllSemesters() {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT * FROM semester ORDER BY sem_id";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("id", String.valueOf(rs.getInt("sem_id")));
                row.put("year", rs.getString("sem_year"));
                row.put("term", rs.getString("sem_term"));
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
