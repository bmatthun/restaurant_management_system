package com.example.demo.controller;

import com.example.demo.model.Bowl;
import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("GET /orders - lista megjelenítése")
    void testListOrders() throws Exception {
        Order order = new Order();
        order.setId(1L);

        when(orderService.findAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/list"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @DisplayName("GET /orders/new - űrlap megjelenítése")
    void testShowCreateForm() throws Exception {
        when(orderService.findAllCustomers()).thenReturn(List.of(new Customer()));
        when(orderService.findAllBowls()).thenReturn(List.of(new Bowl()));

        mockMvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/new"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attributeExists("bowls"));
    }

    @Test
    @DisplayName("POST /orders/new - új rendelés mentése")
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(eq(1L), eq(LocalDate.of(2026, 3, 5)), eq(LocalTime.of(12, 30)),
                anyList(), anyList())).thenReturn(new Order());

        mockMvc.perform(post("/orders/new")
                        .param("customerId", "1")
                        .param("date", "2026-03-05")
                        .param("time", "12:30")
                        .param("bowlIds", "1")
                        .param("quantities", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).createOrder(eq(1L), eq(LocalDate.of(2026, 3, 5)), eq(LocalTime.of(12, 30)),
                anyList(), anyList());
    }

    @Test
    @DisplayName("GET /orders/{id} - részletek megjelenítése")
    void testShowOrderDetail() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(List.of(new OrderItem()));

        when(orderService.findOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/detail"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    @DisplayName("POST /orders/{id}/delete - rendelés törlése")
    void testDeleteOrder() throws Exception {
        mockMvc.perform(post("/orders/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(orderService).deleteOrder(1L);
    }
}
