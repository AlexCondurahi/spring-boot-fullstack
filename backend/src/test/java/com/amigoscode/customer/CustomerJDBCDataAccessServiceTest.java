package com.amigoscode.customer;

import com.amigoscode.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        // before each test, set up this class with all the dependencies that are needed
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        ); // fresh new object
    }

    @Test
    void selectAllCustomers() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );
        underTest.insertCustomer(customer);

        // when
        List<Customer> customers = underTest.selectAllCustomers();

        // then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );
        underTest.insertCustomer(customer);

        // get the customer
        int id = underTest.selectAllCustomers()
                .stream()
                        .filter( c -> c.getEmail().equals(email))
                                .map( c -> c.getId())
                                        .findFirst()
                                                .orElseThrow();


        // when
        Optional<Customer> foundedCustomerOptional = underTest.selectCustomerById(id);

        // then
        assertThat(foundedCustomerOptional).isPresent().hasValueSatisfying(c -> {
           assertThat(c.getId()).isEqualTo(id);
           assertThat(c.getName()).isEqualTo(name);
           assertThat(c.getEmail()).isEqualTo(email);
           assertThat(c.getEmail()).isEqualTo(email);
        });
    }


    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // given
        int id = -1; // make sure id does not exists

        // when
        var actual = underTest.selectCustomerById(id);

        //then
        assertThat(actual).isEmpty();
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
        underTest.insertCustomer(customer);

        // then
        // get the customer
        int insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter( c -> c.getEmail().equals(email) && c.getName().equals(customer.getName()) && c.getAge().equals(customer.getAge()))
                .map( c -> c.getId())
                .findFirst()
                .orElseThrow();

        assertThat(insertedCustomerId).isNotNull();
    }

    @Test
    void existsCustomerWithEmail() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com";

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        underTest.insertCustomer(customer);

        // when
        boolean result = underTest.existsCustomerWithEmail(email);

        // then
        assertThat(result).isTrue();

    }

    @Test
    void deleteCustomer() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        underTest.insertCustomer(customer);

        // get the customer id
        int insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter( c -> c.getEmail().equals(email) && c.getName().equals(customer.getName()) && c.getAge().equals(customer.getAge()))
                .map( c -> c.getId())
                .findFirst()
                .orElseThrow();

        // when
        underTest.deleteCustomer(insertedCustomerId);

        // then
        // check if the inserted customer still exists
        var insertedCustomerIdOptional = underTest.selectAllCustomers()
                .stream()
                .filter( c -> c.getEmail().equals(email) )
                .map( c -> c.getId())
                .findFirst();

        assertThat(insertedCustomerIdOptional).isEmpty();
    }

    @Test
    void existsCustomerWithId() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        underTest.insertCustomer(customer);

        // get the customer id
        int insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter( c -> c.getEmail().equals(email) && c.getName().equals(customer.getName()) && c.getAge().equals(customer.getAge()))
                .map( c -> c.getId())
                .findFirst()
                .orElseThrow();

        // when
        var result = underTest.existsCustomerWithId(insertedCustomerId);

        // then
        assertThat(result).isTrue();
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

        underTest.insertCustomer(customer);

        // get the customer id
        int insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter( c -> c.getEmail().equals(email) && c.getName().equals(customer.getName()) && c.getAge().equals(customer.getAge()))
                .map( c -> c.getId())
                .findFirst()
                .orElseThrow();

        // set customer id
        customer.setId(insertedCustomerId);

        // modify the customer that was inserted on the name field
        var oldName = customer.getName();
        customer.setName("rubvysfhvbhxfdgvzx");

        // when
        underTest.updateCustomer(customer);

        // get the modified customer and check if field
        underTest.selectCustomerById(insertedCustomerId)
                .ifPresent(c -> {
                    // then
                    assertThat(c.getName()).isNotEqualTo(oldName);
                });
    }

    @Test
    void willNotBeEqualForAgeWhenUpdateCustomer() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        underTest.insertCustomer(customer);

        // get the customer id
        int insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter( c -> c.getEmail().equals(email) && c.getName().equals(customer.getName()) && c.getAge().equals(customer.getAge()))
                .map( c -> c.getId())
                .findFirst()
                .orElseThrow();

        // set customer id
        customer.setId(insertedCustomerId);

        // modify the customer that was inserted on the name field
        var oldAge = customer.getAge();
        customer.setAge(40);

        // when
        underTest.updateCustomer(customer);

        // get the modified customer and check if field
        underTest.selectCustomerById(insertedCustomerId)
                .ifPresent(c -> {
                    // then
                    assertThat(c.getAge()).isNotEqualTo(oldAge);
                });
    }

    @Test
    void willNotBeEqualForEmailWhenUpdateCustomer() {
        // Given
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        underTest.insertCustomer(customer);

        // get the customer id
        int insertedCustomerId = underTest.selectAllCustomers()
                .stream()
                .filter( c -> c.getEmail().equals(email) && c.getName().equals(customer.getName()) && c.getAge().equals(customer.getAge()))
                .map( c -> c.getId())
                .findFirst()
                .orElseThrow();

        // set customer id
        customer.setId(insertedCustomerId);

        // modify the customer that was inserted on the name field
        var oldEmail = customer.getEmail();
        customer.setEmail("asdfsdgfgfs@dsfsfdgsdf.com");

        // when
        underTest.updateCustomer(customer);

        // get the modified customer and check if field
        underTest.selectCustomerById(insertedCustomerId)
                .ifPresent(c -> {
                    // then
                    assertThat(c.getEmail()).isNotEqualTo(oldEmail);
                });
    }
}