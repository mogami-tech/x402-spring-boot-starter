package tech.mogami.spring.autoconfigure.parameter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * X402 Parameters.
 *
 * @param facilitator facilitator parameters
 */
@Validated
@SuppressWarnings("unused")
@ConfigurationProperties(prefix = "x402")
public record X402Parameters(

        @Valid
        @NotNull(message = "{validation.facilitator.empty}")
        Facilitator facilitator

) {

    /**
     * Facilitator parameters.
     *
     * @param baseUrl the base URL of the facilitator
     */
    public record Facilitator(

            @NotEmpty(message = "{validation.facilitator.base-url.empty}")
            @URL(message = "{validation.facilitator.base-url.invalid}")
            String baseUrl

    ) {
    }

}

