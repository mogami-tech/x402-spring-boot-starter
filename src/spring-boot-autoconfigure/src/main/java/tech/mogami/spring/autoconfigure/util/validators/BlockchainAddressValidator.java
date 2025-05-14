package tech.mogami.spring.autoconfigure.util.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import static tech.mogami.spring.autoconfigure.util.constants.BlockchainConstants.BLOCKCHAIN_ADDRESS_LENGTH;
import static tech.mogami.spring.autoconfigure.util.constants.BlockchainConstants.BLOCKCHAIN_ADDRESS_PREFIX;

/**
 * Blockchain address validator.
 */
public class BlockchainAddressValidator implements ConstraintValidator<BlockchainAddress, String> {

    @Override
    public final boolean isValid(final String blockchainAddress, final ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(blockchainAddress)) {
            return true;
        }

        // Check the length of the address and if it starts with "0x".
        if (blockchainAddress.length() != BLOCKCHAIN_ADDRESS_LENGTH
                || !blockchainAddress.startsWith(BLOCKCHAIN_ADDRESS_PREFIX)) {
            return false;
        }

        // Check if the address contains only hexadecimal characters.
        return blockchainAddress.substring(BLOCKCHAIN_ADDRESS_PREFIX.length()).matches("^[0-9a-fA-F]+$");
    }

}
