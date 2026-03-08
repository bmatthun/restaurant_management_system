package com.example.demo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderModelTest {

    @Test
    @DisplayName("Az Order összesíti a teljes árat a rendelési tételekből")
    void calculateTotalPriceSumsOrderItems() {
        OrderItem firstItem = new OrderItem();
        firstItem.setLineTotal(1200);
        OrderItem secondItem = new OrderItem();
        secondItem.setLineTotal(2300);

        Order order = new Order();
        order.setOrderItems(List.of(firstItem, secondItem));

        order.calculateTotalPrice();

        assertThat(order.getPrice()).isEqualTo(3500);
    }

    @Test
    @DisplayName("Az OrderItem átveszi a tál árát, ha az egységár még nincs beállítva")
    void calculateTotalsUsesBowlPriceWhenUnitPriceMissing() {
        Bowl bowl = new Bowl();
        bowl.setPrice(1800);

        OrderItem orderItem = new OrderItem();
        orderItem.setBowl(bowl);
        orderItem.setQuantity(3);

        orderItem.calculateTotals();

        assertThat(orderItem.getUnitPrice()).isEqualTo(1800);
        assertThat(orderItem.getLineTotal()).isEqualTo(5400);
    }

    @Test
    @DisplayName("Az OrderItem megtartja a meglévő egységárat az újraszámoláskor")
    void calculateTotalsKeepsExistingUnitPrice() {
        Bowl bowl = new Bowl();
        bowl.setPrice(1800);

        OrderItem orderItem = new OrderItem();
        orderItem.setBowl(bowl);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(1500);

        orderItem.calculateTotals();

        assertThat(orderItem.getUnitPrice()).isEqualTo(1500);
        assertThat(orderItem.getLineTotal()).isEqualTo(3000);
    }
}
