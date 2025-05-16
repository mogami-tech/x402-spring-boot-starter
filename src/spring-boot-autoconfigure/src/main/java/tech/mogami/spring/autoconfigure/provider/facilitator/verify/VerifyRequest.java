package tech.mogami.spring.autoconfigure.provider.facilitator.verify;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * VerifyRequest is a request to the facilitator service.
 *
 * @param x402Version         x402 version
 * @param paymentHeader       payment header
 * @param paymentRequirements payment requirements
 */
@Jacksonized
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record VerifyRequest(
        int x402Version,
        String paymentHeader,
        PaymentRequirements paymentRequirements) {
}
