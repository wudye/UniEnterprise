package com.mwu.dida.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TempController {

    @GetMapping("/temp")
    public String getTemp() {
        return "123";
    }
}
