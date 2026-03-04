package com.example.demo.controller;

import com.example.demo.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Shows the order list.
     *
     * @param model MVC model
     * @return order list view
     */
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAllOrders());
        return "orders/list";
    }

    /**
     * Shows the order creation form.
     *
     * @param model MVC model
     * @return order creation view
     */
    @GetMapping("/orders/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customers", orderService.findAllCustomers());
        model.addAttribute("bowls", orderService.findAllBowls());
        model.addAttribute("defaultDate", LocalDate.now());
        model.addAttribute("defaultTime", LocalTime.now().withNano(0));
        return "orders/new";
    }

    /**
     * Creates a new order.
     *
     * @param customerId selected customer
     * @param date order date
     * @param time order time
     * @param bowlIds selected bowl identifiers
     * @param quantities quantities per bowl
     * @return redirect to order list
     */
    @PostMapping("/orders/new")
    public String createOrder(
            @RequestParam Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam(name = "bowlIds", required = false) List<Long> bowlIds,
            @RequestParam(name = "quantities", required = false) List<Integer> quantities
    ) {
        orderService.createOrder(customerId, date, time, bowlIds, quantities);
        return "redirect:/orders";
    }

    /**
     * Shows order details.
     *
     * @param id order identifier
     * @param model MVC model
     * @return order detail view
     */
    @GetMapping("/orders/{id}")
    public String showOrderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findOrderById(id));
        return "orders/detail";
    }

    /**
     * Deletes an order.
     *
     * @param id order identifier
     * @return redirect to order list
     */
    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }
}
