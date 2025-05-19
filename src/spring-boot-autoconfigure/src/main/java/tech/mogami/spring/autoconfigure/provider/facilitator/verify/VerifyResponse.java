package tech.mogami.spring.autoconfigure.provider.facilitator.verify;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * VerifyResponse is a response from the facilitator service.
 *
 * @param valid         verification status
 * @param invalidReason di
 * @param payer         payer address
 */
@Jacksonized
@Builder
public record VerifyResponse(
        boolean valid,
        String invalidReason,
        String payer) {
}
