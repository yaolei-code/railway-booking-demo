package org.example.studentcoursesystem.controller;

import org.example.studentcoursesystem.dao.SemesterDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SemesterController {

    @GetMapping("/semesters")
    public String viewSemesters(@RequestParam("sid") String sid, Model model) {
        SemesterDAO semesterDAO = new SemesterDAO();
        model.addAttribute("semesters", semesterDAO.getAllSemesters());
        model.addAttribute("sid", sid);
        return "semester";  // 指向 semester.html 页面
    }
}
