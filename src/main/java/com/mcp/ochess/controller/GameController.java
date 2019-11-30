package com.mcp.ochess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game")
public class GameController {
    @RequestMapping("/")
    public String home() {
        return "game";
    }
}
