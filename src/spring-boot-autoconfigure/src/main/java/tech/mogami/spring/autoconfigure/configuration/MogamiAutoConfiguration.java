package tech.mogami.spring.autoconfigure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Mogami Spring Boot Auto-Configuration.
 */
@Slf4j
@AutoConfiguration
public class MogamiAutoConfiguration {

    /**
     * Hello World Application Runner.
     * @return ApplicationRunner
     */
    @Bean
    ApplicationRunner helloWorldRunner() {
        return args -> log.info(">>> Hello World from Mogami spring boot starter !");
    }

}
