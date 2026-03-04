package com.example.demo.service;

import com.example.demo.model.Bowl;
import com.example.demo.model.Ingredient;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.enums.UnitTypes;
import com.example.demo.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceCoverageTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ShoppingListService shoppingListService;

    @Test
    @DisplayName("Bevásárlólista figyelmen kívül hagyja a hiányos rendelési adatokat")
    void weeklyShoppingListSkipsIncompleteOrderData() {
        LocalDate weekStart = LocalDate.of(2026, 3, 2);
        LocalDate weekEnd = LocalDate.of(2026, 3, 8);

        Ingredient validIngredient = createIngredient("Rizs", 200, UnitTypes.G);
        Ingredient repeatedIngredientWithDifferentUnit = createIngredient("Rizs", 50, UnitTypes.KG);
        Ingredient ingredientWithoutUnit = createIngredient("Olaj", 2, null);
        Ingredient missingName = createIngredient(null, 100, UnitTypes.G);
        Ingredient missingQuantity = createIngredient("Szoja", null, UnitTypes.DL);

        Bowl validBowl = new Bowl();
        validBowl.setIngredients(Arrays.asList(
                validIngredient,
                repeatedIngredientWithDifferentUnit,
                ingredientWithoutUnit,
                null,
                missingName,
                missingQuantity
        ));

        Bowl bowlWithoutIngredients = new Bowl();

        List<OrderItem> firstOrderItems = new ArrayList<>();
        firstOrderItems.add(null);
        firstOrderItems.add(createOrderItem(null, 3));
        firstOrderItems.add(createOrderItem(validBowl, null));
        firstOrderItems.add(createOrderItem(bowlWithoutIngredients, 2));
        firstOrderItems.add(createOrderItem(validBowl, 3));

        Order orderWithoutItems = new Order();
        orderWithoutItems.setOrderItems(null);

        Order orderWithMixedItems = new Order();
        orderWithMixedItems.setOrderItems(firstOrderItems);

        when(orderRepository.findByDateBetween(weekStart, weekEnd)).thenReturn(List.of(orderWithoutItems, orderWithMixedItems));

        Map<String, Double> shoppingList = shoppingListService.getWeeklyShoppingList(weekStart);
        Map<String, String> units = shoppingListService.getWeeklyShoppingListUnits(weekStart);

        assertThat(shoppingList).containsOnlyKeys("Rizs", "Olaj");
        assertThat(shoppingList).containsEntry("Rizs", 750.0);
        assertThat(shoppingList).containsEntry("Olaj", 6.0);
        assertThat(units).containsOnlyKeys("Rizs", "Olaj");
        assertThat(units).containsEntry("Rizs", "g");
        assertThat(units).containsEntry("Olaj", "");
        verify(orderRepository, times(2)).findByDateBetween(weekStart, weekEnd);
    }

    private Ingredient createIngredient(String name, Integer quantity, UnitTypes unit) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unit);
        return ingredient;
    }

    private OrderItem createOrderItem(Bowl bowl, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBowl(bowl);
        orderItem.setQuantity(quantity);
        return orderItem;
    }
}
