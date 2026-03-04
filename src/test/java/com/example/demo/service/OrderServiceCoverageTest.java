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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceCoverageTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BowlRepository bowlRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Rendelések dátum szerint csökkenő sorrendbe rendezve térnek vissza")
    void findAllOrdersSortsByDateAndIdDescending() {
        Order olderOrder = createOrder(1L, LocalDate.of(2026, 3, 1));
        Order newestLowerId = createOrder(2L, LocalDate.of(2026, 3, 5));
        Order newestHigherId = createOrder(3L, LocalDate.of(2026, 3, 5));
        Order undatedOrder = createOrder(4L, null);
        when(orderRepository.findAll()).thenReturn(List.of(olderOrder, newestLowerId, undatedOrder, newestHigherId));

        List<Order> result = orderService.findAllOrders();

        assertThat(result).containsExactly(newestHigherId, newestLowerId, olderOrder, undatedOrder);
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Rendelési űrlaphoz minden vendéget visszaad")
    void findAllCustomersReturnsRepositoryCustomers() {
        Customer firstCustomer = new Customer();
        Customer secondCustomer = new Customer();
        when(customerRepository.findAll()).thenReturn(List.of(firstCustomer, secondCustomer));

        List<Customer> result = orderService.findAllCustomers();

        assertThat(result).containsExactly(firstCustomer, secondCustomer);
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Rendelési űrlaphoz minden tálat visszaad")
    void findAllBowlsReturnsRepositoryBowls() {
        Bowl firstBowl = createBowl(10L, 1200);
        Bowl secondBowl = createBowl(11L, 1800);
        when(bowlRepository.findAll()).thenReturn(List.of(firstBowl, secondBowl));

        List<Bowl> result = orderService.findAllBowls();

        assertThat(result).containsExactly(firstBowl, secondBowl);
        verify(bowlRepository).findAll();
    }

    @Test
    @DisplayName("Rendelés lekérdezése id alapján visszaadja a talált entitást")
    void findOrderByIdReturnsOrder() {
        Order order = createOrder(20L, LocalDate.of(2026, 3, 2));
        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));

        Order result = orderService.findOrderById(20L);

        assertThat(result).isSameAs(order);
        verify(orderRepository).findById(20L);
    }

    @Test
    @DisplayName("Rendelés lekérdezése ismeretlen id-vel kivételt dob")
    void findOrderByIdThrowsWhenMissing() {
        when(orderRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrderById(404L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid order id: 404");

        verify(orderRepository).findById(404L);
    }

    @Test
    @DisplayName("Rendelés létrehozása kihagyja az érvénytelen tételeket és alapértelmezett dátumot/időt ad")
    void createOrderUsesDefaultsAndSkipsInvalidItems() {
        Customer customer = new Customer();
        Bowl firstBowl = createBowl(30L, 1000);
        Bowl secondBowl = createBowl(31L, 1500);

        when(customerRepository.findById(7L)).thenReturn(Optional.of(customer));
        when(bowlRepository.findById(30L)).thenReturn(Optional.of(firstBowl));
        when(bowlRepository.findById(31L)).thenReturn(Optional.of(secondBowl));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(
                7L,
                null,
                null,
                Arrays.asList(30L, null, 31L),
                Arrays.asList(2, 4, 1)
        );

        assertThat(result.getCustomer()).isSameAs(customer);
        assertThat(result.getDate()).isEqualTo(LocalDate.now());
        assertThat(result.getTime()).isNotNull();
        assertThat(result.getTime().getNano()).isZero();
        assertThat(result.getQuantity()).isEqualTo("3");
        assertThat(result.getOrderItems()).hasSize(2);
        assertThat(result.getOrderItems().get(0).getUnitPrice()).isEqualTo(1000);
        assertThat(result.getOrderItems().get(0).getLineTotal()).isEqualTo(2000);
        assertThat(result.getOrderItems().get(1).getUnitPrice()).isEqualTo(1500);
        assertThat(result.getOrderItems().get(1).getLineTotal()).isEqualTo(1500);
        assertThat(result.getOrderItems()).allMatch(item -> item.getOrder() == result);
        verify(customerRepository).findById(7L);
        verify(bowlRepository).findById(30L);
        verify(bowlRepository).findById(31L);
        verify(orderRepository).save(result);
    }

    @Test
    @DisplayName("Rendelés létrehozása érvénytelen vendég esetén kivételt dob")
    void createOrderThrowsWhenCustomerMissing() {
        when(customerRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(
                8L,
                LocalDate.of(2026, 3, 6),
                LocalTime.NOON,
                List.of(1L),
                List.of(1)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid customer id: 8");

        verify(customerRepository).findById(8L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Rendelés létrehozása érvénytelen tál id esetén kivételt dob")
    void createOrderThrowsWhenBowlMissing() {
        Customer customer = new Customer();
        when(customerRepository.findById(9L)).thenReturn(Optional.of(customer));
        when(bowlRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(
                9L,
                LocalDate.of(2026, 3, 6),
                LocalTime.NOON,
                List.of(99L),
                List.of(1)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid bowl id: 99");

        verify(customerRepository).findById(9L);
        verify(bowlRepository).findById(99L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Rendelés létrehozása legalább egy érvényes tétel nélkül kivételt dob")
    void createOrderThrowsWhenNoValidItemsRemain() {
        Customer customer = new Customer();
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> orderService.createOrder(
                10L,
                LocalDate.of(2026, 3, 6),
                LocalTime.NOON,
                Arrays.asList(null, 1L),
                Arrays.asList(2, 0)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one order item is required");

        verify(customerRepository).findById(10L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Rendelés törlése a feloldott entitást törli")
    void deleteOrderDeletesResolvedOrder() {
        Order order = createOrder(40L, LocalDate.of(2026, 3, 7));
        when(orderRepository.findById(40L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(40L);

        verify(orderRepository).findById(40L);
        verify(orderRepository).delete(order);
    }

    private Order createOrder(Long id, LocalDate date) {
        Order order = new Order();
        order.setId(id);
        order.setDate(date);
        return order;
    }

    private Bowl createBowl(Long id, Integer price) {
        Bowl bowl = new Bowl();
        bowl.setId(id);
        bowl.setPrice(price);
        return bowl;
    }
}
