package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Összes vendég lekérdezése a repository-ból történik")
    void findAllCustomersReturnsRepositoryResult() {
        Customer firstCustomer = createCustomer(1L, "Anna");
        Customer secondCustomer = createCustomer(2L, "Bela");
        List<Customer> customers = List.of(firstCustomer, secondCustomer);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.findAllCustomers();

        assertThat(result).containsExactly(firstCustomer, secondCustomer);
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Vendég létrehozása menti és visszaadja a repository eredményét")
    void createCustomerSavesCustomer() {
        Customer customer = createCustomer(null, "Csilla");
        Customer persistedCustomer = createCustomer(3L, "Csilla");
        when(customerRepository.save(customer)).thenReturn(persistedCustomer);

        Customer result = customerService.createCustomer(customer);

        assertThat(result).isSameAs(persistedCustomer);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Vendég lekérdezése id alapján visszaadja a talált entitást")
    void findCustomerByIdReturnsCustomer() {
        Customer customer = createCustomer(4L, "David");
        when(customerRepository.findById(4L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findCustomerById(4L);

        assertThat(result).isSameAs(customer);
        verify(customerRepository).findById(4L);
    }

    @Test
    @DisplayName("Vendég lekérdezése ismeretlen id-vel kivételt dob")
    void findCustomerByIdThrowsWhenCustomerMissing() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findCustomerById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid customer id: 99");

        verify(customerRepository).findById(99L);
    }

    @Test
    @DisplayName("Vendég frissítése átveszi az új mezőértékeket és ment")
    void updateCustomerCopiesFieldsAndSavesExistingCustomer() {
        Customer existingCustomer = createCustomer(5L, "Erika");
        existingCustomer.setPhone("111");
        existingCustomer.setEmail("old@example.com");
        existingCustomer.setAddress("Old street");
        existingCustomer.setNote("Old note");

        Customer updatedCustomer = createCustomer(null, "Erika New");
        updatedCustomer.setPhone("222");
        updatedCustomer.setEmail("new@example.com");
        updatedCustomer.setAddress("New street");
        updatedCustomer.setNote("New note");

        when(customerRepository.findById(5L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

        Customer result = customerService.updateCustomer(5L, updatedCustomer);

        assertThat(result).isSameAs(existingCustomer);
        assertThat(existingCustomer.getName()).isEqualTo("Erika New");
        assertThat(existingCustomer.getPhone()).isEqualTo("222");
        assertThat(existingCustomer.getEmail()).isEqualTo("new@example.com");
        assertThat(existingCustomer.getAddress()).isEqualTo("New street");
        assertThat(existingCustomer.getNote()).isEqualTo("New note");
        verify(customerRepository).findById(5L);
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    @DisplayName("Vendég törlése a feloldott entitást törli")
    void deleteCustomerDeletesResolvedCustomer() {
        Customer customer = createCustomer(6L, "Feri");
        when(customerRepository.findById(6L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(6L);

        verify(customerRepository).findById(6L);
        verify(customerRepository).delete(customer);
    }

    private Customer createCustomer(Long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }
}
