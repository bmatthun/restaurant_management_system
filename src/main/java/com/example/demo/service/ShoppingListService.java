package com.example.demo.service;

import com.example.demo.model.Bowl;
import com.example.demo.model.Ingredient;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingListService {

    private final OrderRepository orderRepository;

    public ShoppingListService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Aggregates the required ingredient quantities for the seven-day period
     * starting from the provided date.
     *
     * @param weekStart first day of the shopping week
     * @return ingredient name to aggregated quantity map
     */
    @Transactional
    public Map<String, Double> getWeeklyShoppingList(LocalDate weekStart) {
        Map<String, ShoppingListAggregate> aggregates = aggregateWeeklyIngredients(weekStart);
        Map<String, Double> shoppingList = new LinkedHashMap<>();

        for (Map.Entry<String, ShoppingListAggregate> entry : aggregates.entrySet()) {
            shoppingList.put(entry.getKey(), entry.getValue().quantity());
        }

        return shoppingList;
    }

    /**
     * Resolves display units for the weekly shopping list entries.
     *
     * @param weekStart first day of the shopping week
     * @return ingredient name to display unit map
     */
    @Transactional
    public Map<String, String> getWeeklyShoppingListUnits(LocalDate weekStart) {
        Map<String, ShoppingListAggregate> aggregates = aggregateWeeklyIngredients(weekStart);
        Map<String, String> units = new LinkedHashMap<>();

        for (Map.Entry<String, ShoppingListAggregate> entry : aggregates.entrySet()) {
            units.put(entry.getKey(), entry.getValue().unit());
        }

        return units;
    }

    private Map<String, ShoppingListAggregate> aggregateWeeklyIngredients(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<Order> orders = orderRepository.findByDateBetween(weekStart, weekEnd);
        Map<String, ShoppingListAggregate> aggregates = new LinkedHashMap<>();

        for (Order order : orders) {
            if (order.getOrderItems() == null) {
                continue;
            }

            for (OrderItem orderItem : order.getOrderItems()) {
                accumulateOrderItem(orderItem, aggregates);
            }
        }

        return aggregates;
    }

    private void accumulateOrderItem(OrderItem orderItem, Map<String, ShoppingListAggregate> aggregates) {
        if (orderItem == null || orderItem.getBowl() == null || orderItem.getQuantity() == null) {
            return;
        }

        Bowl bowl = orderItem.getBowl();
        if (bowl.getIngredients() == null) {
            return;
        }

        for (Ingredient ingredient : bowl.getIngredients()) {
            if (ingredient == null || ingredient.getName() == null || ingredient.getQuantity() == null) {
                continue;
            }

            double requiredQuantity = ingredient.getQuantity() * orderItem.getQuantity().doubleValue();
            String ingredientName = ingredient.getName();
            String unit = ingredient.getUnit() == null ? "" : ingredient.getUnit().name().toLowerCase();
            ShoppingListAggregate current = aggregates.get(ingredientName);

            if (current == null) {
                aggregates.put(ingredientName, new ShoppingListAggregate(requiredQuantity, unit));
                continue;
            }

            aggregates.put(
                    ingredientName,
                    new ShoppingListAggregate(current.quantity() + requiredQuantity, current.unit())
            );
        }
    }

    private record ShoppingListAggregate(double quantity, String unit) {
    }
}
