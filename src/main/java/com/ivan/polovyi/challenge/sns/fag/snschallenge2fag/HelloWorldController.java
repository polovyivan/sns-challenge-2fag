package com.ivan.polovyi.challenge.sns.fag.snschallenge2fag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String helloWorld() {

        return "Hello world";
    }
}
