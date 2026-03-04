package com.example.demo.controller;

import com.example.demo.service.ShoppingListService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    /**
     * Displays the weekly shopping list for the requested week.
     *
     * @param weekStart first day of the requested week
     * @param model MVC model
     * @return shopping list template name
     */
    @GetMapping("/shopping-list")
    public String getWeeklyShoppingList(
            @RequestParam("week") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            Model model
    ) {
        Map<String, Double> shoppingList = shoppingListService.getWeeklyShoppingList(weekStart);
        Map<String, String> ingredientUnits = shoppingListService.getWeeklyShoppingListUnits(weekStart);

        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekStart.plusDays(6));
        model.addAttribute("shoppingList", shoppingList);
        model.addAttribute("ingredientUnits", ingredientUnits);
        return "shopping-list";
    }
}
