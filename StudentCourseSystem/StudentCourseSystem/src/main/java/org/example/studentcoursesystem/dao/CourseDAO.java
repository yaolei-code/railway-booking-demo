package org.example.studentcoursesystem.dao;


import org.example.studentcoursesystem.entity.Course;
import org.example.studentcoursesystem.utils.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/student_course_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "050607";

    // 查询所有课程
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = new Course();
                course.setCid(rs.getString("cid"));
                course.setCname(rs.getString("cname"));
                course.setCredit(rs.getInt("credit"));
                courses.add(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    // 根据课程编号查课程名称
    public String findCnameByCid(String cid) {
        String cname = "";
        String sql = "SELECT cname FROM course WHERE cid = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                cname = rs.getString("cname");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cname;
    }

    public Course getCourseById(String cid) {
        String sql = "SELECT * FROM course WHERE cid = '" + cid + "'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                Course course = new Course();
                course.setCid(rs.getString("cid"));
                course.setCname(rs.getString("cname"));
                course.setCredit(rs.getInt("credit"));
                return course;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 第一步：修改 CourseDAO.java，联表查询教师信息

    // 联表查询课程和教师信息
    public List<Map<String, Object>> getCoursesWithTeacher() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
        SELECT c.cid, c.cname, c.credit, t.tname, t.title
        FROM course c
        JOIN teacher t ON c.tid = t.tid
        ORDER BY c.cid
        """;

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("cid", rs.getString("cid"));
                row.put("cname", rs.getString("cname"));
                row.put("credit", rs.getBigDecimal("credit"));
                row.put("tname", rs.getString("tname"));
                row.put("title", rs.getString("title"));
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }








}