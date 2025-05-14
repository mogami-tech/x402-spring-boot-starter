package tech.mogami.spring.autoconfigure.util.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import tech.mogami.spring.autoconfigure.util.enums.Network;

import java.util.Arrays;

/**
 * Blockchain network validator.
 */
public class BlockchainNetworkValidator implements ConstraintValidator<BlockchainNetwork, String> {

    @Override
    public final boolean isValid(final String blockchainNetwork, final ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(blockchainNetwork)) {
            return true;
        }

        return Arrays
                .stream(Network.values())
                .map(Network::getValue)
                .anyMatch(networkName -> StringUtils.equalsIgnoreCase(networkName, blockchainNetwork));
    }

}
