package org.example.studentcoursesystem.dao;

import org.example.studentcoursesystem.entity.Teacher;
import org.example.studentcoursesystem.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    // 查询所有教师
    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teacher";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int tid = rs.getInt("tid");
                String tname = rs.getString("tname");
                String title = rs.getString("title");

                Teacher t = new Teacher(tid, tname, title);
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}