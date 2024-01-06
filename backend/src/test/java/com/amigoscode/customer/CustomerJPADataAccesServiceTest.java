package com.amigoscode.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Random;
import java.util.UUID;

class CustomerJPADataAccesServiceTest {

    private CustomerJPADataAccesService undertest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository; // spring allready comes with all the dependencies that we need

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this); // returns autoclosable which is used to close the resource
        undertest = new CustomerJPADataAccesService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close(); // after each test we have a fresh mock that we can work with
    }

    @Test
    void selectAllCustomers() {
        // verify if the method from the repository is invoked
        // when
        undertest.selectAllCustomers();

        // then
        // with mockkito you can check if a method was called
        Mockito.verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {
        int id = 1;

        // when
        undertest.selectCustomerById(id);

        // then
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        // when
        undertest.insertCustomer(customer);

        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        String email = "random@gmail.com";
        undertest.existsCustomerWithEmail(email);
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomer() {
        int id = 1;
        undertest.deleteCustomer(id);
        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void existsCustomerWithId() {
        int id = 1;
        undertest.existsCustomerWithId(id);
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        // when
        undertest.updateCustomer(customer);

        Mockito.verify(customerRepository).save(customer);
    }
}