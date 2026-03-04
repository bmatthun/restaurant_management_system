package com.example.demo.controller;

import com.example.demo.model.Bowl;
import com.example.demo.repository.BowlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class ThymeLeafController {

    @Autowired
    private BowlRepository bowlRepository;

    @GetMapping("/bowls")
    public String listBowl(Model model) {
        model.addAttribute("bowls", bowlRepository.findAll());
        return "bowls/list";
    }

    @GetMapping("/bowls/new")
    public String showCreateForm(Model model) {
        model.addAttribute("bowl", new Bowl());
        return "bowls/form";
    }

    @GetMapping("/bowls/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Bowl bowl = bowlRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bowl id: " + id));
        model.addAttribute("bowl", bowl);
        return "bowls/form";
    }

    @PostMapping("/bowls")
    public String saveBowl(@ModelAttribute Bowl bowl) {
        bowlRepository.save(bowl);
        return "redirect:/bowls";
    }

    @GetMapping("/bowls/delete/{id}")
    public String deleteBowl(@PathVariable Long id) {
        Bowl bowl = bowlRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bowl id: " + id));
        bowlRepository.delete(bowl);
        return "redirect:/bowls";
    }
}
