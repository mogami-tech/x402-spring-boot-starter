package tech.mogami.spring.autoconfigure.schemes.exact;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.io.IOException;
import java.math.BigInteger;

/**
 * The payment the client will save.
 *
 * @param x402Version the version of the x402 protocol
 * @param scheme      the scheme used for the payment
 * @param network     the network used for the payment
 * @param payload     the payload of the payment
 */
@Jacksonized
@Builder
public record ExactSchemePayment(
        int x402Version,
        String scheme,
        String network,
        Payload payload
) {

    @Jacksonized
    @Builder
    public record Payload(
            String signature,
            Authorization authorization
    ) {

        @Jacksonized
        @Builder
        public record Authorization(
                String from,
                String to,
                BigInteger value,
                long validAfter,
                long validBefore,
                String nonce
        ) {
        }

    }

    /**
     * Convert the raw JSON string to a PaymentHeader object.
     *
     * @param rawJson the raw JSON string
     * @param mapper  the ObjectMapper to use for deserialization
     * @return the PaymentHeader object
     */
    public static ExactSchemePayment fromHeader(final String rawJson, final ObjectMapper mapper) {
        try {
            return mapper.readValue(rawJson, ExactSchemePayment.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid X-PAYMENT header", e);
        }
    }

}