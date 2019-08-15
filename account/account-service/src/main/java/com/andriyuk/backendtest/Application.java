package com.andriyuk.backendtest;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "account",
                version = "0.1",
                description = "Basic account microservice, designed without using heavy frameworks, such as Spring",
                license = @License(name = "Apache 2.0"),
                contact = @Contact(name = "Andrey Andriyuk", email = "Andrey_Andriyuk@protonmail.com")
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}