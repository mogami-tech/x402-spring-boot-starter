package tech.mogami.spring.autoconfigure.dto;

import lombok.Builder;

import java.util.Map;

/**
 * Payment requirement returned to the client when he tries to access a resource.
 *
 * @param scheme            Scheme of the payment protocol to use.
 *                          A schene is a structured definition that specifies the format,
 *                          validation rules and processing logic for a specific type of transaction
 * @param network           Network of the blockchain to send payment on (e.g., 'base-mainnet')
 * @param maxAmountRequired Maximum amount required to pay for the resource in atomic units of the asset (e.g., '0.10')
 * @param resource          URL of resource to pay for
 * @param description       Description of the resource
 * @param mimeType          MIME type of the resource (e.g., application/json)
 * @param payTo             Address to pay value to
 * @param maxTimeoutSeconds Maximum time in seconds for the resource server to respond (e.g., 60)
 * @param asset             Address of the EIP-3009 compliant ERC20 contract (example: an ERC20 contract address).
 * @param extra             Extra information about the payment details specific to the scheme
 *                          For `exact` scheme on a EVM network, expects extra to contain the records `name` and
 *                          `version` pertaining to asset
 */
@Builder
public record PaymentRequirement(
        String scheme,
        String network,
        String maxAmountRequired,
        String resource,
        String description,
        String mimeType,
        String payTo,
        int maxTimeoutSeconds,
        String asset,
        Map<String, String> extra
) {
}
