package com.example.demo.service;

import com.example.demo.model.Ingredient;
import com.example.demo.model.enums.UnitTypes;
import com.example.demo.repository.IngredientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientServiceCrudTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    @DisplayName("Összes hozzávaló lekérdezése a repository-ból történik")
    void findAllIngredientsReturnsRepositoryResult() {
        Ingredient firstIngredient = createIngredient(1L, "Rizs", 200, UnitTypes.G);
        Ingredient secondIngredient = createIngredient(2L, "Olaj", 1, UnitTypes.L);
        when(ingredientRepository.findAll()).thenReturn(List.of(firstIngredient, secondIngredient));

        List<Ingredient> result = ingredientService.findAllIngredients();

        assertThat(result).containsExactly(firstIngredient, secondIngredient);
        verify(ingredientRepository).findAll();
    }

    @Test
    @DisplayName("Hozzávaló létrehozása menti és visszaadja a repository eredményét")
    void createIngredientSavesIngredient() {
        Ingredient ingredient = createIngredient(null, "Só", 10, UnitTypes.DKG);
        Ingredient persistedIngredient = createIngredient(3L, "Só", 10, UnitTypes.DKG);
        when(ingredientRepository.save(ingredient)).thenReturn(persistedIngredient);

        Ingredient result = ingredientService.createIngredient(ingredient);

        assertThat(result).isSameAs(persistedIngredient);
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    @DisplayName("Hozzávaló lekérdezése id alapján visszaadja a talált entitást")
    void findIngredientByIdReturnsIngredient() {
        Ingredient ingredient = createIngredient(4L, "Bors", 5, UnitTypes.DKG);
        when(ingredientRepository.findById(4L)).thenReturn(Optional.of(ingredient));

        Ingredient result = ingredientService.findIngredientById(4L);

        assertThat(result).isSameAs(ingredient);
        verify(ingredientRepository).findById(4L);
    }

    @Test
    @DisplayName("Hozzávaló lekérdezése ismeretlen id-vel kivételt dob")
    void findIngredientByIdThrowsWhenIngredientMissing() {
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.findIngredientById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ingredient id: 99");

        verify(ingredientRepository).findById(99L);
    }

    @Test
    @DisplayName("Hozzávaló frissítése átveszi az új mezőértékeket és ment")
    void updateIngredientCopiesFieldsAndSavesExistingIngredient() {
        Ingredient existingIngredient = createIngredient(5L, "Rizs", 100, UnitTypes.G);
        Ingredient updatedIngredient = createIngredient(null, "Barna rizs", 250, UnitTypes.DKG);

        when(ingredientRepository.findById(5L)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.save(existingIngredient)).thenReturn(existingIngredient);

        Ingredient result = ingredientService.updateIngredient(5L, updatedIngredient);

        assertThat(result).isSameAs(existingIngredient);
        assertThat(existingIngredient.getName()).isEqualTo("Barna rizs");
        assertThat(existingIngredient.getQuantity()).isEqualTo(250);
        assertThat(existingIngredient.getUnit()).isEqualTo(UnitTypes.DKG);
        verify(ingredientRepository).findById(5L);
        verify(ingredientRepository).save(existingIngredient);
    }

    @Test
    @DisplayName("Hozzávaló törlése a feloldott entitást törli")
    void deleteIngredientDeletesResolvedIngredient() {
        Ingredient ingredient = createIngredient(6L, "Citrom", 3, UnitTypes.DB);
        when(ingredientRepository.findById(6L)).thenReturn(Optional.of(ingredient));

        ingredientService.deleteIngredient(6L);

        verify(ingredientRepository).findById(6L);
        verify(ingredientRepository).delete(ingredient);
    }

    private Ingredient createIngredient(Long id, String name, Integer quantity, UnitTypes unitType) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unitType);
        return ingredient;
    }
}
