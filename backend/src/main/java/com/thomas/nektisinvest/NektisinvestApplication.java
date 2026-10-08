package com.thomas.nektisinvest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NektisinvestApplication {

    public static void main(String[] args) {
        SpringApplication.run(NektisinvestApplication.class, args);
    }
}
