package com.amigoscode.customer;

public record CustomerEditRequest(
        String name,
        String email,
        Integer age
) {

}
