package tech.mogami.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.mogami.commons.constant.X402Constants.X402_DEFAULT_PAYMENT_TIMEOUT_SECONDS;
import static tech.mogami.commons.header.payment.schemes.exact.ExactSchemeConstants.EXACT_SCHEME_NAME;

/**
 * X402 Pay USDC annotation.
 */
@Repeatable(X402PayUSDC.List.class)
@Target(METHOD)
@Retention(RUNTIME)
@Documented
@X402Pay
public @interface X402PayUSDC {

    /**
     * Scheme of the payment protocol to use ("exact" by default).
     *
     * @return the scheme
     */
    String scheme() default EXACT_SCHEME_NAME;


    /**
     * Amount required to pay for the resource in units of the asset.
     * example: "10.5" USDC
     *
     * @return the amount
     */
    String amount();

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
     * Recipient wallet address for the payment.
     *
     * @return the address
     */
    String payTo() default "";

    /**
     * Blockchain network identifier (e.g., "base-sepolia", "base").
     *
     * @return the network
     */
    String network() default "";

    /**
     * Human-readable description of the resource.
     *
     * @return the description
     */
    String description() default "";

    /**
     * X402PayUSDC list.
     */
    @Target(METHOD)
    @Retention(RUNTIME)
    @Documented
    @interface List {
        @SuppressWarnings("UnusedReturnValue")
        X402PayUSDC[] value();
    }

}
