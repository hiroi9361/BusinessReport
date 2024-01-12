package analix.DHIT.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AuthController {

//    @GetMapping("/")
//    public String defaultRouting() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
//            return "redirect:/manager/home";
//        } else if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
//            return "redirect:/member/report/create";
//        }
//        return "redirect:/login";
//
//    }

    @GetMapping("/")
    public String defaultRouting(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        //is_Maneger(↓ここでログイン中のemployeeCodeに基づいてis_Managerの値を入れる)今は直接代入
//        session.setAttribute("is_Manager",1);
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            String title = "ホーム";
            model.addAttribute("title", title);
            return "common/common";
        } else if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
            String title = "ホーム";
            model.addAttribute("title", title);
            return "common/common";
        }
        String title = "ホーム";
        model.addAttribute("title", title);

        return "common/login";

    }
    @GetMapping("/login")
    public String displayLogin() {
        return "common/login";
    }

}
