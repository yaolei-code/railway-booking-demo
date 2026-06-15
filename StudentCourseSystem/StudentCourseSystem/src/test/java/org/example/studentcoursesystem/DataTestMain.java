package org.example.studentcoursesystem;

import org.example.studentcoursesystem.dao.StudentDAO;
import org.example.studentcoursesystem.dao.TeacherDAO;
import org.example.studentcoursesystem.entity.Student;
import org.example.studentcoursesystem.entity.Teacher;

import java.util.List;

public class DataTestMain {
    public static void main(String[] args) {
        // 测试学生数据读取
        System.out.println("\n==== 所有学生信息 ====");
        List<Student> studentList = new StudentDAO().getAllStudents();
        for (Student s : studentList) {
            System.out.println(s);
        }

        // 测试教师数据读取
        System.out.println("\n==== 所有教师信息 ====");
        List<Teacher> teacherList = new TeacherDAO().getAllTeachers();
        for (Teacher t : teacherList) {
            System.out.println(t);
        }
    }
}