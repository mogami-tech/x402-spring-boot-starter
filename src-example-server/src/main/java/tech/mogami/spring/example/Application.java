package tech.mogami.spring.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot server example.
 */
@SpringBootApplication
@SuppressWarnings({"checkstyle:FinalClass", "checkstyle:HideUtilityClassConstructor"})
public class Application {

    /**
     * Main method to start the Spring Boot application.
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        org.springframework.boot.SpringApplication.run(Application.class, args);
    }

}
