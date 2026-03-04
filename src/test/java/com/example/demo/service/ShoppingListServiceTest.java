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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ShoppingListService shoppingListService;

    @Test
    @DisplayName("Heti bevásárlólista - azonos nevű összetevők összeadása rendelések alapján")
    void testGetWeeklyShoppingList() {
        Ingredient riceForBowlA = createIngredient("Rizs", 200, UnitTypes.G);
        Ingredient riceForBowlB = createIngredient("Rizs", 100, UnitTypes.G);
        Ingredient chicken = createIngredient("Csirke", 150, UnitTypes.G);

        Bowl bowlA = createBowl(riceForBowlA, chicken);
        Bowl bowlB = createBowl(riceForBowlB);

        OrderItem orderItemA = createOrderItem(bowlA, 3);
        OrderItem orderItemB = createOrderItem(bowlB, 2);

        Order order = new Order();
        order.setDate(LocalDate.of(2026, 3, 2));
        order.setOrderItems(List.of(orderItemA, orderItemB));

        when(orderRepository.findByDateBetween(LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 8)))
                .thenReturn(List.of(order));

        Map<String, Double> shoppingList = shoppingListService.getWeeklyShoppingList(LocalDate.of(2026, 3, 2));
        Map<String, String> units = shoppingListService.getWeeklyShoppingListUnits(LocalDate.of(2026, 3, 2));

        assertThat(shoppingList).containsEntry("Rizs", 800.0);
        assertThat(shoppingList).containsEntry("Csirke", 450.0);
        assertThat(units).containsEntry("Rizs", "g");
        assertThat(units).containsEntry("Csirke", "g");
        verify(orderRepository, times(2))
                .findByDateBetween(LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 8));
    }

    @Test
    @DisplayName("Heti bevásárlólista - üres lista, ha nincs rendelés")
    void testGetWeeklyShoppingListWhenNoOrders() {
        when(orderRepository.findByDateBetween(LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 8)))
                .thenReturn(List.of());

        Map<String, Double> shoppingList = shoppingListService.getWeeklyShoppingList(LocalDate.of(2026, 3, 2));

        assertThat(shoppingList).isEmpty();
    }

    private Ingredient createIngredient(String name, Integer quantity, UnitTypes unit) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unit);
        return ingredient;
    }

    private Bowl createBowl(Ingredient... ingredients) {
        Bowl bowl = new Bowl();
        bowl.setIngredients(List.of(ingredients));
        return bowl;
    }

    private OrderItem createOrderItem(Bowl bowl, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBowl(bowl);
        orderItem.setQuantity(quantity);
        return orderItem;
    }
}
