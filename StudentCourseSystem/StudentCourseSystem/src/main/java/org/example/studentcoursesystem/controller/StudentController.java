package org.example.studentcoursesystem.controller;

import org.example.studentcoursesystem.dao.StudentDAO;
import org.example.studentcoursesystem.entity.Student;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentDAO studentDAO = new StudentDAO();

    @GetMapping
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    // 可以后续添加按 ID 查询、添加学生等方法
}