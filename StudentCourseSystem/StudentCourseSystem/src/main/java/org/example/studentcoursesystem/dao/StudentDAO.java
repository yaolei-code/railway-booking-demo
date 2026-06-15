package org.example.studentcoursesystem.dao;

import org.example.studentcoursesystem.entity.Student;
import org.example.studentcoursesystem.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDAO {

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM student";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student stu = new Student();
                stu.setSid(rs.getInt("sid"));
                stu.setSname(rs.getString("sname"));
                stu.setGender(rs.getString("gender"));
                stu.setMajor(rs.getString("major"));
                list.add(stu);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean verifyLogin(int sid, String sname) {
        String sql = "SELECT COUNT(*) FROM student WHERE sid = ? AND sname = ?";
        try (Connection conn = JDBCUtils.getConnection();  // 改这里
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sid);
            ps.setString(2, sname);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> getStudentInfoWithClassAndDepartment(int sid) {
        Map<String, String> info = new HashMap<>();
        String sql = """
        SELECT s.sname, c.class_name, d.dept_name
        FROM student s
        JOIN class c ON s.class_id = c.class_id
        JOIN department d ON c.dep_id = d.dept_id
        WHERE s.sid = ?
        """;

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info.put("sname", rs.getString("sname"));
                info.put("className", rs.getString("class_name"));
                info.put("depName", rs.getString("dept_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }
}