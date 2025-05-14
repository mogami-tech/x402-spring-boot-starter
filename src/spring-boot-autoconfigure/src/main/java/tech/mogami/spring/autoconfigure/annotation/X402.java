package tech.mogami.spring.autoconfigure.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * X402 annotation.
 * // TODO Maybe we should ask network here.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface X402 {

    /**
     * Payment amount required for access.
     * TODO Change to BigDecimal ???
     *
     * @return maximum amount required
     */
    String maximumAmountRequired();

    /**
     * Developer’s wallet address (receiving payment).
     *
     * @return the address
     */
    String payTo();

    /**
     * Contract address for the transaction (example: an ERC20 contract address).
     *
     * @return the contract address
     */
    String asset();

}
