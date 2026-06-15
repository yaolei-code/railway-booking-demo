package org.example.studentcoursesystem.dao;

import org.example.studentcoursesystem.utils.JDBCUtils;

import java.sql.*;
import java.util.*;

public class PunishmentDAO {

    public List<Map<String, Object>> getPunishmentsBySid(int sid) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT punish_type, punish_reason, punish_date FROM punishment WHERE sid = ? ORDER BY punish_date DESC";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("type", rs.getString("punish_type"));
                row.put("reason", rs.getString("punish_reason"));
                row.put("date", rs.getDate("punish_date"));
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
