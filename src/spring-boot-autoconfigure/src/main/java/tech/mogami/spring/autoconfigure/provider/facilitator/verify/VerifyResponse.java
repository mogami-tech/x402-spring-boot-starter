package tech.mogami.spring.autoconfigure.provider.facilitator.verify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * VerifyResponse is a response from the facilitator service.
 *
 * @param valid  verification status
 * @param reason verification reason
 */
@Jacksonized
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record VerifyResponse(
        boolean valid,
        String reason) {
}
