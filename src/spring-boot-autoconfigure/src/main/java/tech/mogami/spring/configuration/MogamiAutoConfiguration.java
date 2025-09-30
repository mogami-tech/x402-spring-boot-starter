package tech.mogami.spring.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.mogami.spring.interceptor.X402Interceptor;
import tech.mogami.spring.parameter.X402Parameters;

/**
 * Mogami Spring Boot Auto-Configuration.
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties({
        X402Parameters.class
})
@RequiredArgsConstructor
@ComponentScan("tech.mogami.spring.*")
@SuppressWarnings("checkstyle:DesignForExtension")
public class MogamiAutoConfiguration implements WebMvcConfigurer {

    /** Facilitator service. */
    private final X402Interceptor x402Interceptor;

    /**
     * Mogami init method.
     */
    @PostConstruct
    public void init() {
        log.info("Using Mogami x402 spring boot starter");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(x402Interceptor);
    }

}
