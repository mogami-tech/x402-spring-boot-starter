package tech.mogami.spring.autoconfigure.provider.facilitator.supported;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Supported response for GET /supported.
 *
 * @param kinds list of supported (scheme, network) pairs
 */
@Builder
@Jacksonized
@SuppressWarnings("unused")
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

    /**
     * Convert the SupportedResponse to an encoded JSON string.
     *
     * @return the JSON string
     */
    public String toJSON() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize SupportedResponse to JSON", e);
        }
    }

}


