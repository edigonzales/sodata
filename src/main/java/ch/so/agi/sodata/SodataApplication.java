package ch.so.agi.sodata;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@SpringBootApplication
public class SodataApplication {
    //private Logger log = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(SodataApplication.class, args);
    }

    @Bean
    CommandLineRunner init(JdbcClient jdbcClient) {
        return args -> {
            int rows = jdbcClient.sql("SELECT * FROM T_ILI2DB_SETTINGS LIMIT 1").query().listOfRows().size();
            if (rows != 1) {
                throw new IllegalStateException("Validation query failed.");
            }            
        };
    }
}
