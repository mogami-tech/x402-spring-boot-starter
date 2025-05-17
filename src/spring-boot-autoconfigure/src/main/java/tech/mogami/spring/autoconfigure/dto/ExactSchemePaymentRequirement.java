package tech.mogami.spring.autoconfigure.dto;

import lombok.Builder;
import lombok.Singular;

import java.util.Map;

/**
 * Payment requirement returned to the client when he tries to access a resource without.
 *
 * @param scheme            A structured definition that specifies the format, validation rules and processing logic for a specific type of transaction
 * @param network           Blockchain network identifier (e.g., ”base-mainnet”)
 * @param maxAmountRequired Maximum payment amount required (e.g., ”0.10”)
 * @param resource          URL to access
 * @param description       Custom message describing payment details
 * @param mimeType          MIME type of the resource (e.g., application/json)
 * @param payTo             Developer’s wallet address (receiving payment)
 * @param maxTimeoutSeconds Maximum timeout in seconds for the payment (e.g., 60)
 * @param asset             Contract address for the transaction (example: an ERC20 contract address).
 * @param extra             Extra parameters (e.g., { ”USDC”: ”2”, ”key2”: ”value2” })
 */
@Builder
public record ExactSchemePaymentRequirement(
        String scheme,
        String network,
        String maxAmountRequired,
        String resource,
        String description,
        String mimeType,
        String payTo,
        int maxTimeoutSeconds,
        String asset,
        @Singular("extra") Map<String, String> extra
) {
}
