package tech.mogami.spring.autoconfigure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tech.mogami.spring.autoconfigure.parameters.MogamiParameters;

/**
 * Mogami Spring Boot Auto-Configuration.
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MogamiParameters.class)
public class MogamiAutoConfiguration {

    /**
     * Hello World Application Runner.
     *
     * @return ApplicationRunner
     */
    @Bean
    ApplicationRunner helloWorldRunner() {
        return args -> log.info("Using Mogami spring boot starter");
    }

}
