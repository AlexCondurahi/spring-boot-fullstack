package com.amigoscode.customer;

import com.amigoscode.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.context.ApplicationContext;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE )
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        // System.out.println(applicationContext.getBeanDefinitionCount());
    }


    @Test
    void existsCustomerByEmail() {
        String name = "name" + Integer.toString(new Random().nextInt(16,99));
        String email = name + "-" + UUID.randomUUID() + "@gmail.com"; // unique constraint on email

        var customer = new Customer(
                name,
                email,
                new Random().nextInt(16,99)
        );

        underTest.save(customer);

        // when
        boolean result = underTest.existsCustomerByEmail(email);

        // then
        assertThat(result).isTrue();
    }
}