package tech.mogami.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Adds bazaar extension metadata to an x402-protected method.
 * <p>
 * Use this annotation alongside {@link X402PaymentRequirements} or {@link X402PayUSDC} to
 * include bazaar-compatible discovery information in the 402 Payment Required response.
 * </p>
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface X402Bazaar {

    /**
     * HTTP method of the input endpoint (e.g., "GET", "POST").
     *
     * @return the HTTP method
     */
    String inputMethod();

    /**
     * Body type of the input endpoint (e.g., "json", "form-data", "text").
     * Leave empty if no request body.
     *
     * @return the body type
     */
    String inputBodyType() default "";

    /**
     * Example request body as a JSON string.
     * Leave empty if no request body.
     *
     * @return the request body as a JSON string
     */
    String inputBody() default "";

    /**
     * Output response type (e.g., "json", "text").
     * Leave empty if no output specification.
     *
     * @return the output type
     */
    String outputType() default "";

    /**
     * Example response body as a JSON string.
     * Leave empty if no example response.
     *
     * @return the example response as a JSON string
     */
    String outputExample() default "";

    /**
     * JSON Schema (Draft 2020-12) describing the info object, as a JSON string.
     * Leave empty if no schema.
     *
     * @return the schema as a JSON string
     */
    String schema() default "";

}
