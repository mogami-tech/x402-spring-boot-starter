package tech.mogami.spring.autoconfigure.util.enums;

import lombok.Getter;

/**
 * Blockchain Network Enum.
 */
public enum Network {

    /** Base Sepolia. */
    BASE_SEPOLIA("base-sepolia");

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
