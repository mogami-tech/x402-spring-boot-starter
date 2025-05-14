package tech.mogami.spring.autoconfigure.util.constants;

import lombok.experimental.UtilityClass;

/**
 * Blockchain constants.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class BlockchainConstants {

    /** Blockchain address length. */
    public static final int BLOCKCHAIN_ADDRESS_LENGTH = 42;

    /** Blockchain address prefix. */
    public static final String BLOCKCHAIN_ADDRESS_PREFIX = "0x";

    /** Default maximum timeout in seconds for the payment. */
    public static final int DEFAULT_MAX_TIMEOUT_SECONDS = 60;

}
