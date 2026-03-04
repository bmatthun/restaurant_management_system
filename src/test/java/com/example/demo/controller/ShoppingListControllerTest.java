package com.example.demo.controller;

import com.example.demo.service.ShoppingListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ShoppingListController.class)
class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingListService shoppingListService;

    @Test
    @DisplayName("GET /shopping-list - heti bevásárlólista megjelenítése")
    void testGetWeeklyShoppingList() throws Exception {
        Map<String, Double> shoppingList = new LinkedHashMap<>();
        shoppingList.put("Rizs", 800.0);
        shoppingList.put("Csirke", 450.0);

        Map<String, String> units = new LinkedHashMap<>();
        units.put("Rizs", "g");
        units.put("Csirke", "g");

        when(shoppingListService.getWeeklyShoppingList(LocalDate.of(2026, 3, 2))).thenReturn(shoppingList);
        when(shoppingListService.getWeeklyShoppingListUnits(LocalDate.of(2026, 3, 2))).thenReturn(units);

        mockMvc.perform(get("/shopping-list").param("week", "2026-03-02"))
                .andExpect(status().isOk())
                .andExpect(view().name("shopping-list"))
                .andExpect(model().attribute("weekStart", LocalDate.of(2026, 3, 2)))
                .andExpect(model().attribute("weekEnd", LocalDate.of(2026, 3, 8)))
                .andExpect(model().attribute("shoppingList", shoppingList))
                .andExpect(model().attribute("ingredientUnits", units));
    }
}
