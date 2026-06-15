package org.example.studentcoursesystem.controller;

import org.example.studentcoursesystem.dao.CourseDAO;
import org.example.studentcoursesystem.entity.Course;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.Map;

@Controller
public class CourseController {

    private final CourseDAO courseDAO = new CourseDAO();

    @GetMapping("/coursesPage")
    public String showCourses(@RequestParam("sid") String sid, Model model) {
        CourseDAO courseDAO = new CourseDAO();
        List<Course> courseList = courseDAO.getAllCourses();
        model.addAttribute("courses", courseList);
        model.addAttribute("sid", sid);
        return "courses";
    }


    @GetMapping("/coursesWithTeacher")
    public String viewCoursesWithTeacher(@RequestParam("sid") String sid, Model model) {
        CourseDAO courseDAO = new CourseDAO();
        List<Map<String, Object>> courses = courseDAO.getCoursesWithTeacher();

        model.addAttribute("courses", courses);
        model.addAttribute("sid", sid); // 加这一行非常关键！！

        return "coursesWithTeacher";
    }

}