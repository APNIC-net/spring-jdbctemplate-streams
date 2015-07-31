package net.apnic.example.jdbcstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;

import javax.sql.DataSource;

@SpringBootApplication
public class JdbcStreamApplication implements EmbeddedDatabaseConfigurer{

    public static void main(String[] args) {
        SpringApplication.run(JdbcStreamApplication.class, args);
    }

    @Override
    public void configureConnectionProperties(ConnectionProperties connectionProperties, String s) {

    }

    @Override
    public void shutdown(DataSource dataSource, String s) {

    }
}
