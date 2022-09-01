package com.nov.spring.librarycrudspringboot.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {



    @Autowired
    public MainController() {
    }

    @GetMapping
    public String getPeople() {
        return "main/main";
    }


}