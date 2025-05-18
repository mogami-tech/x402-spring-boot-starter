package tech.mogami.spring.autoconfigure.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import tech.mogami.spring.autoconfigure.dto.schemes.ExactSchemePayload;

import java.io.IOException;

import static tech.mogami.spring.autoconfigure.dto.schemes.ExactSchemeConstants.EXACT_SCHEME_NAME;

/**
 * Payment payload (included as the X-PAYMENT header in base64 encoded JSON).
 *
 * @param x402Version version of the x402 payment protocol
 * @param scheme      scheme is the scheme value of the accepted `paymentRequirements` the client is using to pay
 * @param network     network is the network id of the accepted `paymentRequirements` the client is using to pay
 * @param payload     payload is scheme dependent
 */
@Jacksonized
@Builder
public record PaymentPayload(
        int x402Version,
        String scheme,
        String network,
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "scheme")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = ExactSchemePayload.class, name = EXACT_SCHEME_NAME)
        })
        Object payload
) {

    /**
     * Convert the raw JSON string to this object.
     *
     * @param jsonString the raw JSON string
     * @param mapper     the ObjectMapper to use for deserialization
     * @return the exact scheme X-PAYMENT payment object
     */
    public static PaymentPayload fromJSONString(final String jsonString, final ObjectMapper mapper) {
        try {
            return mapper.readValue(jsonString, PaymentPayload.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid X-PAYMENT header", e);
        }
    }

}
