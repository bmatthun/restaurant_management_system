package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Returns all customers.
     *
     * @return customer list
     */
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Creates a new customer.
     *
     * @param customer customer data
     * @return persisted customer
     */
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Finds a customer by id.
     *
     * @param id customer identifier
     * @return existing customer
     */
    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer id: " + id));
    }

    /**
     * Updates an existing customer.
     *
     * @param id customer identifier
     * @param updatedCustomer new values
     * @return persisted customer
     */
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existingCustomer = findCustomerById(id);
        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setPhone(updatedCustomer.getPhone());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setAddress(updatedCustomer.getAddress());
        existingCustomer.setNote(updatedCustomer.getNote());
        return customerRepository.save(existingCustomer);
    }

    /**
     * Deletes a customer by id.
     *
     * @param id customer identifier
     */
    public void deleteCustomer(Long id) {
        customerRepository.delete(findCustomerById(id));
    }
}
