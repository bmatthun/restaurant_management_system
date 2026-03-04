package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds orders between the provided dates, inclusive.
     *
     * @param startDate first day to include
     * @param endDate last day to include
     * @return orders in the given date range
     */
    List<Order> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
