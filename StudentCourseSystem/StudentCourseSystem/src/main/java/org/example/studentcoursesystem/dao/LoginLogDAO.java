package org.example.studentcoursesystem.dao;

import org.example.studentcoursesystem.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginLogDAO {

    public void insertLog(int sid, Timestamp loginTime, String loginIp) {
        String sql = "INSERT INTO login_log (sid, login_time, login_ip) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sid);
            ps.setTimestamp(2, loginTime);
            ps.setString(3, loginIp);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getLogsBySid(int sid) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT login_time, login_ip FROM login_log WHERE sid = ? ORDER BY login_time DESC";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("time", rs.getTimestamp("login_time"));
                row.put("ip", rs.getString("login_ip"));
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}