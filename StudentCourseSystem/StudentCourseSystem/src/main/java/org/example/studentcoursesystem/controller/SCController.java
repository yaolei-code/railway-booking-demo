package org.example.studentcoursesystem.controller;

import org.example.studentcoursesystem.dao.SCDAO;
import org.example.studentcoursesystem.entity.SC;
import org.example.studentcoursesystem.dao.CourseDAO;
import org.example.studentcoursesystem.entity.Course;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.List;

@Controller
public class SCController {

    private final SCDAO scDAO = new SCDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    @PostMapping("/selectCourse")
    public String selectCourse(@RequestParam("sid") int sid,
                               @RequestParam("cid") String cid,
                               @RequestParam("semester") String semester,
                               Model model) {

        Course course = courseDAO.getCourseById(cid);
        model.addAttribute("cid", cid);
        model.addAttribute("semester", semester);
        model.addAttribute("cname", course != null ? course.getCname() : "未知课程");
        model.addAttribute("sid", sid); // 用于后续跳转时保留 sid

        if (scDAO.isAlreadySelected(sid, cid, semester)) {
            model.addAttribute("msg", "你已经选过这门课啦，不能重复选课～");
            return "selectResult";
        }

        SC sc = new SC();
        sc.setSid(sid);
        sc.setCid(cid);
        sc.setSemester(semester);

        try {
            scDAO.insertSC(sc);
            model.addAttribute("msg", "选课成功！");
        } catch (Exception e) {
            model.addAttribute("msg", "选课失败，请联系管理员！");
        }

        return "selectResult";
    }

    // 显示学生已选课程列表
    @GetMapping("/myCourses")
    public String viewSelectedCourses(@RequestParam("sid") int sid, Model model) {
        List<SC> scList = scDAO.getSelectedCoursesBySid(sid);
        List<Course> courseList = new ArrayList<>();

        for (SC sc : scList) {
            Course course = courseDAO.getCourseById(sc.getCid());
            if (course != null) {
                courseList.add(course);
            }
        }

        model.addAttribute("selectedCourses", courseList);
        model.addAttribute("sid", sid);  // ✅ 把学号传给前端页面
        return "myCourses";
    }

    @GetMapping("/myScores")
    public String viewScores(@RequestParam("sid") int sid, Model model) {
        SCDAO scDAO = new SCDAO();
        List<Map<String, Object>> scores = scDAO.getScoresByStudentId(sid);

        model.addAttribute("scores", scores);
        model.addAttribute("sid", sid); // 为返回按钮等保留学生ID
        return "myScores"; // 返回 myScores.html 页面
    }
}