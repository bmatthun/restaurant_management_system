package com.example.demo.service;

import com.example.demo.model.Ingredient;
import com.example.demo.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Returns all ingredients.
     *
     * @return ingredient list
     */
    public List<Ingredient> findAllIngredients() {
        return ingredientRepository.findAll();
    }

    /**
     * Creates a new ingredient.
     *
     * @param ingredient ingredient data
     * @return persisted ingredient
     */
    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    /**
     * Finds an ingredient by id.
     *
     * @param id ingredient identifier
     * @return existing ingredient
     */
    public Ingredient findIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ingredient id: " + id));
    }

    /**
     * Updates an existing ingredient.
     *
     * @param id ingredient identifier
     * @param updatedIngredient new values
     * @return persisted ingredient
     */
    public Ingredient updateIngredient(Long id, Ingredient updatedIngredient) {
        Ingredient existingIngredient = findIngredientById(id);
        existingIngredient.setName(updatedIngredient.getName());
        existingIngredient.setQuantity(updatedIngredient.getQuantity());
        existingIngredient.setUnit(updatedIngredient.getUnit());
        return ingredientRepository.save(existingIngredient);
    }

    /**
     * Deletes an ingredient by id.
     *
     * @param id ingredient identifier
     */
    public void deleteIngredient(Long id) {
        ingredientRepository.delete(findIngredientById(id));
    }
}
