package tech.mogami.spring.autoconfigure.provider.facilitator.supported;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Facilitator → client payload for GET /supported.
 *
 * @param kinds list of supported (scheme, network) pairs
 */
@Jacksonized
@Builder
public record SupportedResponse(
        List<SupportedKind> kinds
) {

    /**
     * Single pair the facilitator can handle.
     *
     * @param x402Version x402 version
     * @param scheme      the scheme used for the payment
     * @param network     the network used for the payment
     */
    public record SupportedKind(
            int x402Version,
            String scheme,
            String network) {
    }

}


