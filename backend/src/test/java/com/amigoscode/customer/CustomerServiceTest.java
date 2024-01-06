package com.amigoscode.customer;

import com.amigoscode.exception.DublicateResourceException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDAO customerDAO;


    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomer() {
        // given block is not needed , in the given block we create an environment
        // when (exectute all the logic that is needed to be tested)
        underTest.getAllCustomer();

        // then (in this block we assert, and check if the results are matching our expectations
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void getCustomer() {


    }

    @Test
    void canGetCustomer() {
        // given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 10
        );

        // tell to mockito what it should returns becouse it know nothing about the logic
        // we hardcode the return
        // we define what the dependency should do and return in certain scenarios
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // when
        Customer actual = underTest.getCustomer(id);

        // then
        assertThat(actual).isEqualTo(customer);

        // test another scenrio
        // we also need to test if the customer is not found, if the function throws an error
        // test if functions returs empty optional
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        // given
        int id = 10;
        Mockito.when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty()); // we can controll what certain functions will return

        // when
        // then (what assertion should we make? , well we expect an error, and we create a try catch for that
        // and check the error message from the error)
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id: " + id + " doesn't exist");
    }

    @Test
    void addCustomer() {
        // given
        String email = "alex@gmail.com";

        // when
        Mockito.when(customerDAO.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistratioRequest request = new CustomerRegistratioRequest(
                "Alex", email, 20
        );

        underTest.addCustomer(request);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture()); // captureaza orice argument pasat metodei insertCustomer care este de tip Customer

        Customer capturedCustomer = customerArgumentCaptor.getValue(); // extract the value to perform some assertions

        // check if the inserted customer has the exact same fields from the request
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }


    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // given
        String email = "alex@gmail.com";
        // when
        Mockito.when(customerDAO.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistratioRequest request = new CustomerRegistratioRequest(
                "Alex", email, 20
        );
        // when
        // then
        assertThatThrownBy( () -> underTest.addCustomer(request))
                .isInstanceOf(DublicateResourceException.class)
                .hasMessageContaining("email already taken");

        // verify that will never insert a customer
        verify(customerDAO, Mockito.never()).insertCustomer(Mockito.any());
    }


    @Test
    void deleteCustomer() {
        int id = 10;

        // test if customer don't exist
        Mockito.when(customerDAO.existsCustomerWithId(id)).thenReturn(true);

        // when
        underTest.deleteCustomer(id);

        //then
        verify(customerDAO).deleteCustomer(id);
    }

    @Test
    void willShowErrorWhenCustomerIdDoesntExist() {
        int id = 10;
        // test if customer don't exist
        Mockito.when(customerDAO.existsCustomerWithId(id)).thenReturn(false);

        // when
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("customer with id: [%s] doesn't exist".formatted(id));
        //then
        verify(customerDAO, Mockito.never()).deleteCustomer(id);
    }

    @Test
    void canUpdateNameAgeWhenUpdateCustomer() {
        // given
        // create parameters
        int customerId = 10;
        CustomerEditRequest request = new CustomerEditRequest(
                "alexandru", "alex2001@yahoo.com", 21
        );

        // create the customer for the getCustomer call
        Customer customer = new Customer(
                customerId, "alex", "alex2001@yahoo.com", 20
        );

        // instead of mocking, we can create the customer and other dependencies, or just mock them
        Mockito.when(customerDAO.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // when
        underTest.updateCustomer(customerId, request);

        // then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }


    // test updating just the name
    @Test
    void canUpdateOnlyNameWhenUpdateCustomer() {
        // given
        // create parameters
        int customerId = 10;
        CustomerEditRequest request = new CustomerEditRequest(
                "alexandru", null, null
        );

        // create the customer for the getCustomer call
        Customer customer = new Customer(
                customerId, "alex", "alex2001@yahoo.com", 20
        );

        // instead of mocking, we can create the customer and other dependencies, or just mock them
        Mockito.when(customerDAO.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // when
        underTest.updateCustomer(customerId, request);

        // then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
    }

    // test updating just the age
    @Test
    void canUpdateOnlyAgeWhenUpdateCustomer() {
        // given
        // create parameters
        int customerId = 10;
        CustomerEditRequest request = new CustomerEditRequest(
                null, null, 25
        );

        // create the customer for the getCustomer call
        Customer customer = new Customer(
                customerId, "alex", "alex2001@yahoo.com", 20
        );

        // instead of mocking, we can create the customer and other dependencies, or just mock them
        Mockito.when(customerDAO.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // when
        underTest.updateCustomer(customerId, request);

        // then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    // test updating just the email
    @Test
    void canUpdateOnlyEmailWhenUpdateCustomer() {
        // given
        // create parameters
        int customerId = 10;
        CustomerEditRequest request = new CustomerEditRequest(
                null, "alex2002@yahoo.com", null
        );

        // create the customer for the getCustomer call
        Customer customer = new Customer(
                customerId, "alex", "alex2001@yahoo.com", 20
        );

        // instead of mocking, we can create the customer and other dependencies, or just mock them
        Mockito.when(customerDAO.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // when
        underTest.updateCustomer(customerId, request);

        // then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
    }


}