package tech.mogami.spring.autoconfigure.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.mogami.spring.autoconfigure.interceptor.X402Interceptor;
import tech.mogami.spring.autoconfigure.parameters.MogamiParameters;

/**
 * Mogami Spring Boot Auto-Configuration.
 */
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties(MogamiParameters.class)
@RequiredArgsConstructor
public class MogamiAutoConfiguration implements WebMvcConfigurer {

    /** Mogami parameters. */
    private final MogamiParameters mogamiParameters;

    /**
     * Runner.
     *
     * @return ApplicationRunner
     */
    @Bean
    ApplicationRunner runner() {
        return args -> log.info("Using Mogami spring boot starter for x402");
    }

    @Bean
    public X402Interceptor x402Interceptor() {
        return new X402Interceptor(mogamiParameters);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(x402Interceptor());
    }

}
