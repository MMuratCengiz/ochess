package com.mcp.ochess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {
    @RequestMapping("/")
    public String root() {
        return "forward:/lobby";
    }

    @RequestMapping("/lobby")
    public String listLobbies() {
        return "lobby";
    }

    @RequestMapping("/profile")
    public String showProfile() {
        return "lobby";
    }

    @RequestMapping("/help")
    public String help() {
        return "lobby";
    }

    @RequestMapping("/logout")
    public String logout() {
        return "lobby";
    }
}
