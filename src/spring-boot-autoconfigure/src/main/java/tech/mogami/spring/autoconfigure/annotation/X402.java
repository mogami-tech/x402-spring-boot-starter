package tech.mogami.spring.autoconfigure.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * X402 annotation.
 * // TODO Maybe we should ask network here.
 */
@Repeatable(X402.List.class)
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
     * Wallet address receiving the payment.
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

    /**
     * The network to use.
     *
     * @return the network
     */
    String network();

    /**
     * X402 list.
     */
    @Target(METHOD)
    @Retention(RUNTIME)
    @interface List {
        X402[] value();
    }

}
