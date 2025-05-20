package tech.mogami.spring.autoconfigure.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import tech.mogami.spring.autoconfigure.dto.schemes.ExactSchemePayload;

import java.io.IOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static tech.mogami.spring.autoconfigure.dto.schemes.ExactSchemeConstants.EXACT_SCHEME_NAME;

/**
 * Payment payload (included as the X-PAYMENT header in base64 encoded JSON).
 *
 * @param x402Version version of the x402 payment protocol
 * @param scheme      scheme is the scheme value of the accepted `paymentRequirements` the client is using to pay
 * @param network     network is the network id of the accepted `paymentRequirements` the client is using to pay
 * @param payload     payload is scheme dependent
 */
@Builder
@Jacksonized
@SuppressWarnings("unused")
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
    public static PaymentPayload loadFromJSONString(final String jsonString, final ObjectMapper mapper) {
        try {
            return mapper.readValue(jsonString, PaymentPayload.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid X-PAYMENT header", e);
        }
    }

    /**
     * Convert the PaymentPayload to a base64 encoded JSON string.
     *
     * @return the base64 encoded JSON string
     */
    public String toBase64() {
        try {
            String json = new ObjectMapper().writeValueAsString(this);
            return Base64.getEncoder().withoutPadding().encodeToString(json.getBytes(UTF_8));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize PaymentPayload to JSON", e);
        }
    }

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
