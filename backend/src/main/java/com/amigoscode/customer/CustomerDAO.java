package com.amigoscode.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer customerId);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
    void deleteCustomer(Integer customerId);
    boolean existsCustomerWithId(Integer customerId);
    void updateCustomer(Customer customer);

}
