package com.mcp.ochess.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ingame")
public class GameController {
    @RequestMapping("/")
    public String home(Model model) {
        SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return "game";
    }
}
