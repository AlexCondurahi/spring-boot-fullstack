package com.amigoscode.journey;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerController;
import com.amigoscode.customer.CustomerEditRequest;
import com.amigoscode.customer.CustomerRegistratioRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT
)
public class CustomerIntegrationTest {
    // this is a test that will fire up the entire application
    // and send http requests

    // we can inject the web client and perform http requests
    // webTestClient can be used in a mock environment or a real environment

    // In order to use the http client we need to install the flux pachage for spring boot
    // spring-boot-starter-webflux

    @Autowired
    private WebTestClient webTestClient; // this is our postman with which we can send http requests to our server

    private static final Random RANDOM = new Random();
    private static final String customerURI = "/api/v1/customers";

//    @Autowired
//    private CustomerController CustomerController; // never do this we want to invoke the methods through requests

    @Test
    void canRegisterCostumer() {
        // this is a journey actually , the user creates a customer, lists all the customers in the listing page, and then clicks on a customer
        // integration tests will be runed against the database

        // create registration request
        String name = UUID.randomUUID() + "alex";
        String email = name + "@amigoscode.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistratioRequest customerRegistratioRequest =
                new CustomerRegistratioRequest(name, email, age);

        // send a post request

        webTestClient
                .post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistratioRequest), CustomerRegistratioRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all costumers
        List<Customer> allCustomers = webTestClient
                .get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that the customer is present
        Customer expectedCustomer = new Customer(
                name, email, age
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        int id = allCustomers
                    .stream()
                    .filter( c -> c.getName().equals(name) && c.getEmail().equals(email) && c.getAge().equals(age))
                    .findFirst()
                    .get()
                    .getId();

        expectedCustomer.setId(id);

        // get customer by id from our api
        webTestClient
                .get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        String name = UUID.randomUUID() + "alex";
        String email = name + "@amigoscode.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistratioRequest customerRegistratioRequest =
                new CustomerRegistratioRequest(name, email, age);

        // send a post request

        webTestClient
                .post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistratioRequest), CustomerRegistratioRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all costumers
        List<Customer> allCustomers = webTestClient
                .get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that the customer is present
        Customer expectedCustomer = new Customer(
                name, email, age
        );


        int id = allCustomers
                .stream()
                .filter( c -> c.getName().equals(name) && c.getEmail().equals(email) && c.getAge().equals(age))
                .findFirst()
                .get()
                .getId();

        // delete the customer using id
        webTestClient
                .delete()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();


        // get customer by id from our api
        webTestClient
                .delete()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound(); // expect to not found it

    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        String name = UUID.randomUUID() + "alex";
        String email = name + "@amigoscode.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistratioRequest customerRegistratioRequest =
                new CustomerRegistratioRequest(name, email, age);

        // send a post request

        webTestClient
                .post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistratioRequest), CustomerRegistratioRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all costumers
        List<Customer> allCustomers = webTestClient
                .get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that the customer is present
        Customer expectedCustomer = new Customer(
                name, email, age
        );


        int id = allCustomers
                .stream()
                .filter( c -> c.getName().equals(name) && c.getEmail().equals(email) && c.getAge().equals(age))
                .findFirst()
                .get()
                .getId();

        String newName = "alex20000";

        CustomerEditRequest customerEditRequest = new CustomerEditRequest(
                newName, null, null
        );

        // update the inserted customer
       webTestClient
                .put()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerEditRequest), CustomerEditRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // make assertions - checks - verifications of results
        // get customer by id from our api
        Customer updatedCustomer = webTestClient
                .get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id, newName, email, age
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }

    // we can also test specific journeys , and we can write separate integration test classes for those journeys , and it should do what the client journey is from screen a to screen b
}
