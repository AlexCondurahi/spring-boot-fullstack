package com.amigoscode.customer;

public record CustomerRegistratioRequest(
        String name,
        String email,
        Integer age
) {

}
