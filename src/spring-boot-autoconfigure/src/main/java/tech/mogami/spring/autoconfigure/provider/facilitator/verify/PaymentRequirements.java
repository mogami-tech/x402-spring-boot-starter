package tech.mogami.spring.autoconfigure.provider.facilitator.verify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigInteger;

/**
 * PaymentRequirements is a request to the facilitator service.
 *
 * @param scheme            scheme
 * @param network           network
 * @param payTo             Address to pay to
 * @param asset             asset
 * @param maxAmountRequired maximum amount required
 */
@Jacksonized
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentRequirements(
        String scheme,
        String network,
        String payTo,
        String asset,
        @JsonSerialize(using = ToStringSerializer.class)
        BigInteger maxAmountRequired) {
}
