package com.amigoscode.customer;

import com.amigoscode.exception.DublicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomer() {
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer customerId) {
        return customerDAO
                .selectCustomerById(customerId)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "customer with id: " + customerId + " doesn't exist"
                ) );
    }

    public void addCustomer(CustomerRegistratioRequest customerRegistratioRequest) {
        // check if email exists
        String email = customerRegistratioRequest.email();
        if (customerDAO.existsCustomerWithEmail(email)) {
            throw new DublicateResourceException(
                    "email already taken"
            );
        }
        // add
        Customer customer = new Customer(
                customerRegistratioRequest.name(),
                customerRegistratioRequest.email(),
                customerRegistratioRequest.age()
        );
        customerDAO.insertCustomer(
                customer
        );
    }

    public void deleteCustomer(Integer customerId) {
        if (!customerDAO.existsCustomerWithId(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id: [%s] doesn't exist".formatted(customerId)
            );
        }

        customerDAO.deleteCustomer(customerId);
    }

    // update based on a body request
    public void updateCustomer(Integer customerId, CustomerEditRequest request) {
        // check if the customer exists to have something which we can compare
        var customerCopy = getCustomer(customerId).clone();
        var customer = getCustomer(customerId);

        // check if name and age is different than null
        if (request.name() != null && request.age() != null) {
            customerCopy.setName(request.name());
            if (customerCopy.equals(customer)) {
                throw new RequestValidationException("The name field can't be the same");
            }

            customer.setName(request.name());
            customerCopy.setAge(request.age());
            if (customerCopy.equals(customer)) {
                throw new RequestValidationException("The age field can't be the same");
            }
            customerDAO.updateCustomer(customerCopy);
            return;
        }

        if (request.age() != null) {
            customerCopy.setAge(request.age());
            if (customerCopy.equals(customer)) {
                throw new RequestValidationException("The age field can't be the same");
            }
            customerDAO.updateCustomer(customerCopy);
            return;
        }

        if (request.name() != null) {
            customerCopy.setName(request.name());
            if (customerCopy.equals(customer)) {
                throw new RequestValidationException("The name field can't be the same");
            }
            customerDAO.updateCustomer(customerCopy);
            return;
        }

        if (request.email() != null) {
            customerCopy.setEmail(request.email());
            if (customerCopy.equals(customer)) {
                throw new RequestValidationException("The email field can't be the same");
            }
            customerDAO.updateCustomer(customerCopy);
        }
    }
}
