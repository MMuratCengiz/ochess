package com.mcp.ochess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/register")
    public String register() {
        return "register";
    }
}
