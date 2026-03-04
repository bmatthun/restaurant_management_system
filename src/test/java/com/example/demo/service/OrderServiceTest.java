package com.example.demo.service;

import com.example.demo.model.Bowl;
import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.repository.BowlRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BowlRepository bowlRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Új rendelés létrehozása - tételek és összdarab számítása")
    void testCreateOrder() {
        Customer customer = new Customer();
        customer.setId(1L);

        Bowl bowl = new Bowl();
        bowl.setId(2L);
        bowl.setPrice(2500);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bowlRepository.findById(2L)).thenReturn(Optional.of(bowl));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.createOrder(
                1L,
                LocalDate.of(2026, 3, 5),
                LocalTime.of(12, 30),
                List.of(2L),
                List.of(3)
        );

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        assertThat(order.getCustomer()).isEqualTo(customer);
        assertThat(order.getQuantity()).isEqualTo("3");
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(3);
        assertThat(order.getOrderItems().get(0).getUnitPrice()).isEqualTo(2500);
        assertThat(order.getOrderItems().get(0).getLineTotal()).isEqualTo(7500);
        assertThat(orderCaptor.getValue().getOrderItems().get(0).getOrder()).isEqualTo(orderCaptor.getValue());
    }
}
