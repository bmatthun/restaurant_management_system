package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Shows the customer list.
     *
     * @param model MVC model
     * @return customer list view
     */
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAllCustomers());
        return "customers/list";
    }

    /**
     * Shows the customer creation form.
     *
     * @param model MVC model
     * @return customer form view
     */
    @GetMapping("/customers/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("formAction", "/customers/new");
        return "customers/form";
    }

    /**
     * Creates a customer.
     *
     * @param customer submitted customer data
     * @return redirect to customer list
     */
    @PostMapping("/customers/new")
    public String createCustomer(@ModelAttribute Customer customer) {
        customerService.createCustomer(customer);
        return "redirect:/customers";
    }

    /**
     * Shows the customer edit form.
     *
     * @param id customer identifier
     * @param model MVC model
     * @return customer form view
     */
    @GetMapping("/customers/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.findCustomerById(id));
        model.addAttribute("formAction", "/customers/" + id + "/edit");
        return "customers/form";
    }

    /**
     * Updates a customer.
     *
     * @param id customer identifier
     * @param customer submitted customer data
     * @return redirect to customer list
     */
    @PostMapping("/customers/{id}/edit")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute Customer customer) {
        customerService.updateCustomer(id, customer);
        return "redirect:/customers";
    }

    /**
     * Deletes a customer.
     *
     * @param id customer identifier
     * @return redirect to customer list
     */
    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }
}
