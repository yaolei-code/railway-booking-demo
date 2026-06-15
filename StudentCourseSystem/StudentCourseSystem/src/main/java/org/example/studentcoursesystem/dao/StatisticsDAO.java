package org.example.studentcoursesystem.dao;

import org.example.studentcoursesystem.utils.JDBCUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StatisticsDAO {

    public Map<String, Object> getStudentTotalCredit(String sid, String semester) {
        Map<String, Object> result = new HashMap<>();
        Connection conn = null;
        CallableStatement stmt = null;
        try {
            conn = JDBCUtils.getConnection();
            stmt = conn.prepareCall("{CALL get_student_total_credit(?, ?, ?)}");

            stmt.setString(1, sid);
            stmt.setString(2, semester);
            stmt.registerOutParameter(3, Types.DECIMAL);

            stmt.execute();

            result.put("total_credit", stmt.getBigDecimal(3));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
        return result;
    }
}
