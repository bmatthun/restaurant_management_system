package com.example.demo.service;

import com.example.demo.model.Bowl;
import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.BowlRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BowlRepository bowlRepository;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            BowlRepository bowlRepository
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.bowlRepository = bowlRepository;
    }

    /**
     * Returns all orders sorted by date descending.
     *
     * @return order list
     */
    public List<Order> findAllOrders() {
        List<Order> orders = new ArrayList<>(orderRepository.findAll());
        orders.sort(Comparator.comparing(Order::getDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Order::getId, Comparator.nullsLast(Comparator.reverseOrder())));
        return orders;
    }

    /**
     * Returns all customers for order forms.
     *
     * @return customer list
     */
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Returns all bowls for order forms.
     *
     * @return bowl list
     */
    public List<Bowl> findAllBowls() {
        return bowlRepository.findAll();
    }

    /**
     * Finds an order by id.
     *
     * @param id order identifier
     * @return existing order
     */
    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order id: " + id));
    }

    /**
     * Creates a new order with one or more order items.
     *
     * @param customerId customer identifier
     * @param date order date
     * @param time order time
     * @param bowlIds selected bowl identifiers
     * @param quantities selected bowl quantities
     * @return persisted order
     */
    @Transactional
    public Order createOrder(
            Long customerId,
            LocalDate date,
            LocalTime time,
            List<Long> bowlIds,
            List<Integer> quantities
    ) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer id: " + customerId));
        List<OrderItem> orderItems = buildOrderItems(bowlIds, quantities);
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setDate(date == null ? LocalDate.now() : date);
        order.setTime(time == null ? LocalTime.now().withNano(0) : time);
        order.setQuantity(String.valueOf(orderItems.stream().mapToInt(OrderItem::getQuantity).sum()));
        order.setOrderItems(orderItems);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
        }

        return orderRepository.save(order);
    }

    /**
     * Deletes an order by id.
     *
     * @param id order identifier
     */
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.delete(findOrderById(id));
    }

    private List<OrderItem> buildOrderItems(List<Long> bowlIds, List<Integer> quantities) {
        List<OrderItem> orderItems = new ArrayList<>();
        int itemCount = Math.min(
                bowlIds == null ? 0 : bowlIds.size(),
                quantities == null ? 0 : quantities.size()
        );

        for (int index = 0; index < itemCount; index++) {
            Long bowlId = bowlIds.get(index);
            Integer quantity = quantities.get(index);

            if (bowlId == null || quantity == null || quantity <= 0) {
                continue;
            }

            Bowl bowl = bowlRepository.findById(bowlId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid bowl id: " + bowlId));
            OrderItem orderItem = new OrderItem();
            orderItem.setBowl(bowl);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(bowl.getPrice());
            orderItem.setLineTotal(quantity * bowl.getPrice());
            orderItems.add(orderItem);
        }

        return orderItems;
    }
}
