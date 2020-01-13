package com.mcp.ochess.controller;

import com.mcp.ochess.dao.OChessUserDetailsService;
import com.mcp.ochess.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class RootController {
    @RequestMapping("/")
    public String root() {
        return "forward:/lobby";
    }

    @GetMapping("/game/lobby")
    public String listLobbies() {
        return "lobby";
    }

    @RequestMapping("/game/profile")
    public String showProfile() {
        return "lobby";
    }

    @RequestMapping("/help")
    public String help() {
        return "lobby";
    }

    @RequestMapping("/accessdenied")
    public String accessdenied() {
        return "accessdenied";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, ModelMap mm) {
        if (bindingResult.hasErrors()) {
            return "onerror";
        }

        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(WebRequest request, Model model) {
        User u = new User();
        model.addAttribute("user", u);
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, WebRequest request,
                           Errors errors) {
        OChessUserDetailsService service = new OChessUserDetailsService();
        service.storeUser(user);
        return "register";
    }
}
