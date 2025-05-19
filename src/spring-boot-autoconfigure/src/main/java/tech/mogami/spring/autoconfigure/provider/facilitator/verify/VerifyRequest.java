package tech.mogami.spring.autoconfigure.provider.facilitator.verify;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import tech.mogami.spring.autoconfigure.dto.PaymentPayload;
import tech.mogami.spring.autoconfigure.dto.PaymentRequirements;

/**
 * VerifyRequest is a request to the facilitator service.
 *
 * @param x402Version         x402 version
 * @param paymentPayload      paument payload
 * @param paymentRequirements payment requirements
 */
@Jacksonized
@Builder
public record VerifyRequest(
        int x402Version,
        PaymentPayload paymentPayload,
        PaymentRequirements paymentRequirements) {

    /**
     * Convert the PaymentPayload to an encoded JSON string.
     *
     * @return the JSON string
     */
    public String toJSON() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize PaymentPayload to JSON", e);
        }
    }

}
