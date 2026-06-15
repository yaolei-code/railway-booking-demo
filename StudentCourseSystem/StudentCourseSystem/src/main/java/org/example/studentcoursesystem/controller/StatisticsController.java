package org.example.studentcoursesystem.controller;

import org.example.studentcoursesystem.dao.StatisticsDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class StatisticsController {

    @GetMapping("/totalCredit")
    public String getTotalCredit(@RequestParam("sid") String sid,
                                 @RequestParam("semester") String semester,
                                 Model model) {

        StatisticsDAO dao = new StatisticsDAO();
        Map<String, Object> result = dao.getStudentTotalCredit(sid, semester);

        model.addAttribute("sid", sid);
        model.addAttribute("semester", semester);
        model.addAttribute("totalCredit", result.get("total_credit"));

        return "totalCredit";
    }
}
