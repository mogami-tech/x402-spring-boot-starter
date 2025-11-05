package tech.mogami.spring.annotation;

import tech.mogami.spring.pricing.AmountProvider;
import tech.mogami.spring.pricing.DefaultAmountProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.mogami.commons.constant.X402Constants.X402_DEFAULT_PAYMENT_TIMEOUT_SECONDS;

/**
 * X402 Payment requirements annotation.
 */
@Repeatable(X402PaymentRequirements.List.class)
@Retention(RUNTIME)
@Target({METHOD})
@Documented
@X402Pay
public @interface X402PaymentRequirements {

    /**
     * Scheme of the payment protocol to use (e.g., "exact").
     *
     * @return the scheme
     */
    String scheme();

    /**
     * Blockchain network identifier (e.g., "base-sepolia", "ethereum-mainnet").
     *
     * @return the network
     */
    String network();

    /**
     * Maximum amount required to pay for the resource in atomic units of the asset.
     * For cryptocurrencies with high precision, this allows representing amounts
     * in their smallest denomination (wei, satoshi, etc.) without precision loss.
     * It won't be taken into account if an AmountProvider is provided.
     *
     * @return maximum amount required in the smallest denomination of the cryptocurrency
     */
    String maximumAmountRequired();

    /**
     * Class providing the amount to pay.
     * If you want to use a fixed price, use the maximumAmountRequired field.
     * But if you want to calculate a price depending on the user request,
     * provide your AmountProvider implementation, maximumAmountRequired field will be ignored.
     *
     * @return the amount provider class
     */
    Class<? extends AmountProvider> amountProvider() default DefaultAmountProvider.class;

    /**
     * Maximum timeout required to complete the payment in seconds.
     *
     * @return the timeout
     */
    int maximumTimeoutSeconds() default X402_DEFAULT_PAYMENT_TIMEOUT_SECONDS;

    /**
     * MIME type of the resource.
     *
     * @return the MIME type
     */
    String mimeType() default APPLICATION_JSON_VALUE;

    /**
     * Optional schema describing the structure or metadata of the protected resource output.
     *
     * @return the output schema
     */
    String outputSchema() default "";

    /**
     * Human-readable description of the resource.
     *
     * @return the description
     */
    String description() default "";

    /**
     * Recipient wallet address for the payment.
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
    @Documented
    @interface List {
        @SuppressWarnings("UnusedReturnValue")
        X402PaymentRequirements[] value();
    }

}
