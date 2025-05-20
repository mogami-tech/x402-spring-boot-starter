package tech.mogami.spring.autoconfigure.provider.facilitator.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * VerifyResponse is a response from the facilitator service.
 *
 * @param isValid       verification status
 * @param invalidReason reason why the verification failed
 * @param payer         payer address
 */
@Jacksonized
@Builder
public record VerifyResponse(
        boolean isValid,
        String invalidReason,
        String payer) {


    /**
     * Convert the VerifyResponse to an encoded JSON string.
     *
     * @return the JSON string
     */
    public String toJSON() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize VerifyResponse to JSON", e);
        }
    }

}
