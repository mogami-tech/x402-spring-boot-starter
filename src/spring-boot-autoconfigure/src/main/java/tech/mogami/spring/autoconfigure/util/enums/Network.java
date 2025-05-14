package tech.mogami.spring.autoconfigure.util.enums;

import lombok.Getter;

/**
 * Blockchain networks.
 */
public enum Network {

    /** Base Sepolia. */
    BASE_SEPOLIA("base-sepolia"),

    /** Base Mainnet. */
    BASE_MAINNET("base-mainnet");

    /** Network name. */
    @Getter
    private final String value;

    /**
     * Constructor.
     *
     * @param newValue the network name
     */
    Network(final String newValue) {
        this.value = newValue;
    }

}
