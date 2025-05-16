package tech.mogami.spring.autoconfigure.provider.facilitator.supported;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Single pair the facilitator can handle.
 *
 * @param x402Version x402 version
 * @param scheme      the scheme used for the payment
 * @param network     the network used for the payment
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SupportedKind(
        int x402Version,
        String scheme,
        String network) {
}
