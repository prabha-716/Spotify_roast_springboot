package com.example.spotify_roast.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class AuthController {

    @GetMapping("/")
    public String login(){
        return "login";
    }

    @GetMapping("/login")
    public String home(){
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal OAuth2User user){
        model.addAttribute("name",user.getAttribute("display_name"));
        model.addAttribute("email",user.getAttribute("email"));

        return "dashboard";
    }

    @GetMapping("/profile")
    @ResponseBody
    public OAuth2User profile(@AuthenticationPrincipal OAuth2User user) {
        return user;
    }
    @GetMapping("/me")
    @ResponseBody
    public Map<String,Object> me(@AuthenticationPrincipal OAuth2User user){
        return user.getAttributes();
    }
}
