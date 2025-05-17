package tech.mogami.spring.autoconfigure.schemes;

import lombok.Builder;
import tech.mogami.spring.autoconfigure.schemes.exact.ExactSchemePaymentRequirement;

import java.util.List;

/**
 * Reply when an access request is made to a x402 protected URL.
 *
 * @param x402Version x402 version
 * @param error       error message
 * @param accepts     list of Accept objects
 */
@Builder
public record PaymentRequired(
        int x402Version,
        String error,
        List<ExactSchemePaymentRequirement> accepts
) {
}
