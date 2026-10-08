package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomePageController {
    
    @GetMapping(value = "/")
    public ModelAndView home(ModelAndView mv) {
        mv.setViewName("homePage"); 
        return mv;
    }
}