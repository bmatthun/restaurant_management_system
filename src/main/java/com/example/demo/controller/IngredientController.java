package com.example.demo.controller;

import com.example.demo.model.Ingredient;
import com.example.demo.model.enums.UnitTypes;
import com.example.demo.service.IngredientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * Shows the ingredient list.
     *
     * @param model MVC model
     * @return ingredient list view
     */
    @GetMapping("/ingredients")
    public String listIngredients(Model model) {
        model.addAttribute("ingredients", ingredientService.findAllIngredients());
        return "ingredients/list";
    }

    /**
     * Shows the ingredient creation form.
     *
     * @param model MVC model
     * @return ingredient form view
     */
    @GetMapping("/ingredients/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        model.addAttribute("unitTypes", UnitTypes.values());
        model.addAttribute("formAction", "/ingredients/new");
        return "ingredients/form";
    }

    /**
     * Creates an ingredient.
     *
     * @param ingredient submitted ingredient data
     * @return redirect to ingredient list
     */
    @PostMapping("/ingredients/new")
    public String createIngredient(@ModelAttribute Ingredient ingredient) {
        ingredientService.createIngredient(ingredient);
        return "redirect:/ingredients";
    }

    /**
     * Shows the ingredient edit form.
     *
     * @param id ingredient identifier
     * @param model MVC model
     * @return ingredient form view
     */
    @GetMapping("/ingredients/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("ingredient", ingredientService.findIngredientById(id));
        model.addAttribute("unitTypes", UnitTypes.values());
        model.addAttribute("formAction", "/ingredients/" + id + "/edit");
        return "ingredients/form";
    }

    /**
     * Updates an ingredient.
     *
     * @param id ingredient identifier
     * @param ingredient submitted ingredient data
     * @return redirect to ingredient list
     */
    @PostMapping("/ingredients/{id}/edit")
    public String updateIngredient(@PathVariable Long id, @ModelAttribute Ingredient ingredient) {
        ingredientService.updateIngredient(id, ingredient);
        return "redirect:/ingredients";
    }

    /**
     * Deletes an ingredient.
     *
     * @param id ingredient identifier
     * @return redirect to ingredient list
     */
    @PostMapping("/ingredients/{id}/delete")
    public String deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return "redirect:/ingredients";
    }
}
