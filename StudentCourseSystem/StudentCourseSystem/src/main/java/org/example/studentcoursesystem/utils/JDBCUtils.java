package org.example.studentcoursesystem.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/student_course_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";  // 改成你的用户名
    private static final String PASSWORD = "050607";  // 改成你的密码

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接失败");
        }
    }
}