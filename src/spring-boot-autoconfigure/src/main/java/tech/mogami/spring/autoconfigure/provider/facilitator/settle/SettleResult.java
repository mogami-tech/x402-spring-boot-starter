package tech.mogami.spring.autoconfigure.provider.facilitator.settle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Settle result returned by the x402 facilitator for a settle attempt.
 *
 * @param success     true if the payment succeeded
 * @param network     identifier of the blockchain network, or null
 * @param transaction blockchain transaction hash of the settled payment, or null
 * @param errorReason error message from the facilitator, or null
 * @param payer       payer address, or null
 */
public record SettleResult(
        boolean success,
        String network,
        String transaction,
        String errorReason,
        String payer
) {

    /**
     * Convert the SettleResult to an encoded JSON string.
     *
     * @return the JSON string
     */
    public String toJSON() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize SettleResult to JSON", e);
        }
    }

}
