package org.example.studentcoursesystem.dao;

import org.example.studentcoursesystem.entity.SC;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SCDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/student_course_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "050607";

    // 查询全部选课记录
    public List<SC> getAllSC() {
        List<SC> list = new ArrayList<>();
        String sql = "SELECT * FROM sc";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SC sc = new SC();
                sc.setSid(rs.getInt("sid"));
                sc.setCid(rs.getString("cid"));
                sc.setSemester(rs.getString("semester"));
                list.add(sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 插入选课记录
    public void insertSC(SC sc) {
        String sql = "INSERT INTO sc (sid, cid, semester) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sc.getSid());
            pstmt.setString(2, sc.getCid());
            pstmt.setString(3, sc.getSemester());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断学生是否已选某门课程（用于防止重复选课）
    public boolean isAlreadySelected(int sid, String cid, String semester) {
        String sql = "SELECT COUNT(*) FROM sc WHERE sid = ? AND cid = ? AND semester = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sid);
            pstmt.setString(2, cid);
            pstmt.setString(3, semester);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 根据学号查询某学生的选课记录
    public List<SC> getSelectedCoursesBySid(int sid) {
        List<SC> selectedCourses = new ArrayList<>();
        String sql = "SELECT * FROM sc WHERE sid = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sid);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SC sc = new SC();
                sc.setSid(rs.getInt("sid"));
                sc.setCid(rs.getString("cid"));
                sc.setSemester(rs.getString("semester"));
                selectedCourses.add(sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedCourses;
    }

    // 查询学生成绩和课程信息
    public List<java.util.Map<String, Object>> getScoresByStudentId(int sid) {
        List<java.util.Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT score.cid, course.cname, course.credit, score.score " +
                "FROM score " +
                "JOIN course ON score.cid = course.cid " +
                "WHERE score.sid = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sid);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                row.put("cid", rs.getString("cid"));
                row.put("name", rs.getString("cname"));  // cname 对应 course 表的课程名称
                row.put("credit", rs.getInt("credit"));
                row.put("score", rs.getInt("score"));
                result.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}