package com.primecart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PmtServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmtServiceApplication.class, args);
    }
}
