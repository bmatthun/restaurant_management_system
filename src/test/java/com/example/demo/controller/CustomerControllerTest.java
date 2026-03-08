package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("GET /customers - lista megjelenítése")
    void testListCustomers() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Kiss Anna");

        when(customerService.findAllCustomers()).thenReturn(List.of(customer));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(view().name("customers/list"))
                .andExpect(model().attributeExists("customers"));
    }

    @Test
    @DisplayName("GET /customers/new - új vendég űrlap megjelenítése")
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/customers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("customers/form"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attribute("formAction", "/customers/new"));
    }

    @Test
    @DisplayName("POST /customers/new - új vendég mentése")
    void testCreateCustomer() throws Exception {
        when(customerService.createCustomer(any(Customer.class))).thenReturn(new Customer());

        mockMvc.perform(post("/customers/new")
                        .param("name", "Kiss Anna")
                        .param("phone", "+3612345678")
                        .param("email", "anna@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerService).createCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("GET /customers/{id}/edit - szerkesztő űrlap megjelenítése")
    void testShowEditForm() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Kiss Anna");
        when(customerService.findCustomerById(1L)).thenReturn(customer);

        mockMvc.perform(get("/customers/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("customers/form"))
                .andExpect(model().attribute("customer", customer))
                .andExpect(model().attribute("formAction", "/customers/1/edit"));
    }

    @Test
    @DisplayName("POST /customers/{id}/edit - vendég frissítése")
    void testUpdateCustomer() throws Exception {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("Kiss Anna");
        when(customerService.updateCustomer(any(Long.class), any(Customer.class))).thenReturn(updatedCustomer);

        mockMvc.perform(post("/customers/1/edit")
                        .param("name", "Kiss Anna")
                        .param("phone", "+3612345678")
                        .param("email", "anna@example.com")
                        .param("address", "Budapest")
                        .param("note", "VIP"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerService).updateCustomer(any(Long.class), any(Customer.class));
    }

    @Test
    @DisplayName("POST /customers/{id}/delete - vendég törlése")
    void testDeleteCustomer() throws Exception {
        mockMvc.perform(post("/customers/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerService).deleteCustomer(1L);
    }
}
