package com.example.demo.controller;

import com.example.demo.model.Bowl;
import com.example.demo.repository.BowlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bowls")
public class BowlController {

    @Autowired
    private BowlRepository bowlRepository;

    @GetMapping
    public List<Bowl> getAllBowl() {
        return bowlRepository.findAll();
    }
}
