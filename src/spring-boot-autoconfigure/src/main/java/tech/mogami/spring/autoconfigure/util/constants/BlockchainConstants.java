package tech.mogami.spring.autoconfigure.util.constants;

import lombok.experimental.UtilityClass;

/**
 * Blockchain constants.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class BlockchainConstants {

    /** A transaction hash is always 32 bytes (64 characters). */
    public static final int TRANSACTION_LENGTH = 64;

    /** A transaction hash is always 33 bytes (66 characters) with a prefix. */
    public static final int TRANSACTION_LENGTH_WITH_PREFIX = 66;

    /** Transaction shortens prefix length. */
    public static final int SHORTEN_TRANSACTION_PREFIX_LENGTH = 5;

    /** Transaction shortens suffix length. */
    public static final int SHORTEN_TRANSACTION_SUFFIX_LENGTH = 3;

    /** Ethereum transaction prefix. */
    public static final String TRANSACTION_PREFIX = "0x";

    /** Blockchain address length. */
    public static final int BLOCKCHAIN_ADDRESS_LENGTH = 42;

    /** Blockchain shorten prefix length. */
    public static final int BLOCKCHAIN_ADDRESS_SHORTEN_PREFIX_LENGTH = 5;

    /** Blockchain shorten suffix length. */
    public static final int BLOCKCHAIN_ADDRESS_SHORTEN_SUFFIX_LENGTH = 3;

    /** Blockchain address prefix. */
    public static final String ETHEREUM_ADDRESS_PREFIX = "0x";

    /** Contract address in logs. */
    public static final String CONTRACT_ADDRESS_IN_LOGS = "0x0000000000000000000000000000000000000000";

    /** Blockchain amount type precision. */
    public static final int AMOUNT_TYPE_PRECISION = 18;

    /** Blockchain amount type scale. */
    public static final int AMOUNT_TYPE_SCALE = 8;

}
