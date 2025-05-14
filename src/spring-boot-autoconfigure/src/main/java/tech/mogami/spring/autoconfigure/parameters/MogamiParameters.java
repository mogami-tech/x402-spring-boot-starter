package tech.mogami.spring.autoconfigure.parameters;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import tech.mogami.spring.autoconfigure.util.validators.BlockchainAddress;
import tech.mogami.spring.autoconfigure.util.validators.BlockchainNetwork;

/**
 * Mogami Parameters.
 *
 * @param defaultReceiverAddress default receiver address
 * @param facilitatorUrl         facilitator URL
 * @param network                network to use (e.g., base-sepolia, testnet, mainnet)
 */
@Validated
@ConfigurationProperties(prefix = "mogami")
public record MogamiParameters(

        @NotEmpty
        @BlockchainAddress(message = "{validation.blockchainAddress.invalid}")
        String defaultReceiverAddress,

        @NotEmpty
        @URL(message = "{validation.facilitatorURL.invalid}")
        String facilitatorUrl,

        @NotEmpty
        @BlockchainNetwork(message = "{validation.blockchainNetwork.invalid}")
        String network

) {
}
