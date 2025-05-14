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

        @NotEmpty(message = "{validation.defaultReceiverAddress.empty}")
        @BlockchainAddress(message = "{validation.defaultReceiverAddress.invalid}")
        String defaultReceiverAddress,

        @NotEmpty(message = "{validation.facilitatorURL.empty}")
        @URL(message = "{validation.facilitatorURL.invalid}")
        String facilitatorUrl,

        @NotEmpty(message = "{validation.network.empty}")
        @BlockchainNetwork(message = "{validation.network.invalid}")
        String network

) {
}
