package com.example.campusstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/catalog";
    }

    @GetMapping("/forbidden")
    public String forbidden() {
        return "forbidden";
    }
}