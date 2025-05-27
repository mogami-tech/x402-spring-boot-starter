package tech.mogami.spring.autoconfigure.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * X402 Payment requirements annotation.
 * TODO Treat "mimeType: string;" (MIME type of the resource response)
 * TODO Treat "outputSchema?: object | null;" (Output schema of the resource response)
 */
@Repeatable(X402PaymentRequirements.List.class)
@Retention(RUNTIME)
@Target(METHOD)
public @interface X402PaymentRequirements {

    /**
     * Scheme of the payment protocol to use.
     *
     * @return the scheme
     */
    String scheme();

    /**
     * Network of the blockchain to send payment on.
     *
     * @return the network
     */
    String network();

    /**
     * Maximum amount required to pay for the resource in atomic units of the asset.
     * For cryptocurrencies with high precision, this allows representing amounts
     * in their smallest denomination (wei, satoshi, etc.) without precision loss.
     *
     * @return maximum amount required in the smallest denomination of the cryptocurrency
     */
    String maximumAmountRequired();

    /**
     * Description of the resource.
     *
     * @return the description
     */
    String description() default "";

    /**
     * Address to pay value to.
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
     * Extra information about the payment details specific to the scheme.
     * For `exact` scheme on an EVM network, expect extra to contain the records `name` and `version`
     * pertaining to asset
     *
     * @return Extra information about the payment details specific to the scheme
     */
    ExtraEntry[] extra() default {};

    /**
     * Extra entry.
     */
    @interface ExtraEntry {

        /**
         * Key of the extra entry.
         *
         * @return the key
         */
        String key();

        /**
         * Value of the extra entry.
         *
         * @return the value
         */
        String value();

    }

    /**
     * X402 list.
     */
    @Target(METHOD)
    @Retention(RUNTIME)
    @interface List {
        @SuppressWarnings("UnusedReturnValue")
        X402PaymentRequirements[] value();
    }

}
