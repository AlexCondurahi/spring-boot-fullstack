package com.amigoscode.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomer();
    }

    @GetMapping("{customerId}")
    public Customer getCustomer(
            @PathVariable("customerId") Integer customerId
    ) {

        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public void registerCustomer(
        @RequestBody CustomerRegistratioRequest request
    ) {
        customerService.addCustomer(request);
    }


    @DeleteMapping("{customerId}")
    public void deleteCustomer(
            @PathVariable("customerId") Integer customerId
    ) {
        customerService.deleteCustomer(customerId);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(
            @PathVariable("customerId") Integer customerId,
            @RequestBody CustomerEditRequest request
    ) {
        customerService.updateCustomer(customerId, request);
    }
}



























