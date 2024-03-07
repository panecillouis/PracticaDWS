package com.godfathercapybara;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.godfathercapybara.capybara.service.DatabaseInitializer;

@SpringBootApplication
public class CapybaraApplication {
   

    public static void main(String[] args) {
        SpringApplication.run(CapybaraApplication.class, args);
    }

}