package com.vbforge.projectstracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class TestErrorController {

    //to invoke 500:
    //http://localhost:8080/test-500
    @GetMapping("/test-500")
    public String triggerError() {
        throw new RuntimeException("Simulated server crash");
    }


    //or to invoke 404:
    //http://localhost:8080/projects/99999
}