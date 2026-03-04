package com.example.demo.controller;

import com.example.demo.model.Ingredient;
import com.example.demo.model.enums.UnitTypes;
import com.example.demo.service.IngredientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(IngredientController.class)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientService ingredientService;

    @Test
    @DisplayName("GET /ingredients - lista megjelenítése")
    void testListIngredients() throws Exception {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Rizs");

        when(ingredientService.findAllIngredients()).thenReturn(List.of(ingredient));

        mockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredients/list"))
                .andExpect(model().attributeExists("ingredients"));
    }

    @Test
    @DisplayName("GET /ingredients/new - űrlap megjelenítése")
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/ingredients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredients/form"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("unitTypes"))
                .andExpect(model().attribute("formAction", "/ingredients/new"));
    }

    @Test
    @DisplayName("POST /ingredients/new - új hozzávaló mentése")
    void testCreateIngredient() throws Exception {
        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(new Ingredient());

        mockMvc.perform(post("/ingredients/new")
                        .param("name", "Rizs")
                        .param("quantity", "250")
                        .param("unit", "G"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));

        verify(ingredientService).createIngredient(any(Ingredient.class));
    }

    @Test
    @DisplayName("GET /ingredients/{id}/edit - szerkesztő űrlap megjelenítése")
    void testShowEditForm() throws Exception {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setName("Olaj");
        ingredient.setQuantity(1);
        ingredient.setUnit(UnitTypes.L);

        when(ingredientService.findIngredientById(2L)).thenReturn(ingredient);

        mockMvc.perform(get("/ingredients/2/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredients/form"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("unitTypes"))
                .andExpect(model().attribute("formAction", "/ingredients/2/edit"));
    }

    @Test
    @DisplayName("POST /ingredients/{id}/edit - hozzávaló frissítése")
    void testUpdateIngredient() throws Exception {
        when(ingredientService.updateIngredient(eq(3L), any(Ingredient.class))).thenReturn(new Ingredient());

        mockMvc.perform(post("/ingredients/3/edit")
                        .param("name", "Bors")
                        .param("quantity", "5")
                        .param("unit", "DKG"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));

        verify(ingredientService).updateIngredient(eq(3L), any(Ingredient.class));
    }

    @Test
    @DisplayName("POST /ingredients/{id}/delete - hozzávaló törlése")
    void testDeleteIngredient() throws Exception {
        mockMvc.perform(post("/ingredients/4/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredients"));

        verify(ingredientService).deleteIngredient(4L);
    }
}
