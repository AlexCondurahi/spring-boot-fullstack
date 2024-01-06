package com.amigoscode.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDAO{
    // db
    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();
        var alex = new Customer(
                1,
                "Alex",
                "alex@gmail.com",
                22
        );
        customers.add(alex);

        var jamila = new Customer(
                2,
                "Jamila",
                "jamila@gmail.com",
                19
        );
        customers.add(jamila);
    }


    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream().anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        customers.stream()
                .filter( customer -> customer.getId()
                .equals(customerId) )
                .findFirst()
                .ifPresent(customer -> customers.remove(customer));
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        return false;
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.stream()
                .filter(c -> c.equals(customer))
                .findFirst()
                .ifPresent(c -> {
                    var foundCustomerIndex = customers.indexOf(c);
                    customers.set(foundCustomerIndex, customer);
                });
    }


}
