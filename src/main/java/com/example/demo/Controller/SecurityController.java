package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/403")
    public String accessDenied() {
        // Trả về file 403.html trong thư mục templates
        return "403";
    }
}