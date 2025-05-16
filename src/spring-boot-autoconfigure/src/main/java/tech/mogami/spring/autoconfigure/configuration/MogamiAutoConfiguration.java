package tech.mogami.spring.autoconfigure.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.mogami.spring.autoconfigure.interceptor.X402Interceptor;
import tech.mogami.spring.autoconfigure.parameters.X402Parameters;
import tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorClient;

import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_SUPPORTED_VERSION;

/**
 * Mogami Spring Boot Auto-Configuration.
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties({
        X402Parameters.class,
        X402Parameters.Facilitator.class
})
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class MogamiAutoConfiguration implements WebMvcConfigurer {

    /** Mogami parameters. */
    private final X402Parameters x402Parameters;

    /**
     * Mogami init method.
     */
    @PostConstruct
    public void init() {
        log.info("Using Mogami spring boot starter for x402");
    }

    @Bean
    public FacilitatorClient facilitatorClient() {
        return new FacilitatorClient(
                X402_SUPPORTED_VERSION,
                x402Parameters.facilitator());
    }

    @Bean
    public X402Interceptor x402Interceptor() {
        return new X402Interceptor(x402Parameters);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(x402Interceptor());
    }

}
