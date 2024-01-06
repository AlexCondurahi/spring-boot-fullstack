package com.amigoscode;
import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Main.class, args);
    }

    public static void printBeans(ConfigurableApplicationContext ctx) {
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {

        String name = UUID.randomUUID() + "alex";
        String email = name + "@amigoscode.com";
        int age = new Random().nextInt(1, 100);

        return args -> {
            var alex = new Customer(
                    name,
                    email,
                    new Random().nextInt(16,99)
            );

            List<Customer> customers = List.of(alex);
            customerRepository.saveAll(customers);
        };
    }
}


