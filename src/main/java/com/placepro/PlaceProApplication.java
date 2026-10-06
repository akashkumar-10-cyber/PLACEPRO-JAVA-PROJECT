package com.placepro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PlaceProApplication {

    private static final Logger log = LoggerFactory.getLogger(PlaceProApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PlaceProApplication.class, args);
    }

    @Bean
    public CommandLineRunner printWebsiteLink() {
        return args -> {
            log.info("=======================================================================");
            log.info("🚀 PLACEPRO PORTAL STARTED SUCCESSFULLY!");
            log.info("🔗 CLICKABLE WEBSITE LINK: http://localhost:8080/");
            log.info("📊 DEPT PLACEMENT RECORDS: http://localhost:8080/admin/department-records");
            log.info("=======================================================================");
        };
    }
}
