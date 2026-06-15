package org.example.studentcoursesystem.controller;

import jakarta.servlet.http.HttpSession; import org.example.studentcoursesystem.dao.LoginLogDAO;
import org.example.studentcoursesystem.dao.PunishmentDAO;
import org.example.studentcoursesystem.dao.StudentDAO; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;

import java.time.ZoneId; import java.time.ZonedDateTime; import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Controller public class LoginController {

    // 登录页面
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 登录提交
    @PostMapping("/login")
    public String doLogin(@RequestParam String sid,
                          @RequestParam String sname,
                          HttpSession session,
                          Model model) {
        int id = Integer.parseInt(sid);
        StudentDAO studentDAO = new StudentDAO();

        if (studentDAO.verifyLogin(id, sname)) {
            session.setAttribute("sid", sid);

            //  获取东八区当前时间
            ZonedDateTime chinaTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            Timestamp timestamp = Timestamp.valueOf(chinaTime.toLocalDateTime());

            //  写入登录日志
            LoginLogDAO logDAO = new LoginLogDAO();
            logDAO.insertLog(id, timestamp, "127.0.0.1");  // 真实部署后可用 request.getRemoteAddr()

            return "redirect:/index?sid=" + sid;
        } else {
            model.addAttribute("error", "学号或姓名错误，请重新输入");
            return "login";
        }
    }

    @GetMapping("/index")
    public String index(@RequestParam("sid") String sid, Model model) {
        int id = Integer.parseInt(sid);
        StudentDAO studentDAO = new StudentDAO();
        Map<String, String> info = studentDAO.getStudentInfoWithClassAndDepartment(id);

        model.addAttribute("sid", sid);
        model.addAttribute("studentName", info.get("sname"));
        model.addAttribute("className", info.get("className"));
        model.addAttribute("depName", info.get("depName"));

        return "index";
    }

    @GetMapping("/myLogs")
    public String viewMyLogs(@RequestParam("sid") String sid, Model model) {
        int id = Integer.parseInt(sid);
        LoginLogDAO logDAO = new LoginLogDAO();
        List<Map<String, Object>> logs = logDAO.getLogsBySid(id);

        model.addAttribute("logs", logs);
        model.addAttribute("sid", sid);
        return "myLogs";
    }

    @GetMapping("/myPunishments")
    public String viewMyPunishments(@RequestParam("sid") String sid, Model model) {
        int id = Integer.parseInt(sid);
        PunishmentDAO dao = new PunishmentDAO();
        List<Map<String, Object>> punishments = dao.getPunishmentsBySid(id);

        model.addAttribute("punishments", punishments);
        model.addAttribute("sid", sid);
        return "myPunishments";
    }


}
