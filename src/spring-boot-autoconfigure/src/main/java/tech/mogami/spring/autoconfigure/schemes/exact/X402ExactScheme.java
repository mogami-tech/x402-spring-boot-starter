package tech.mogami.spring.autoconfigure.schemes.exact;

import tech.mogami.spring.autoconfigure.schemes.X402;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * X402 exact scheme annotation.
 * The exact scheme on EVM chains uses EIP-3009 to authorize a transfer of a specific amount of an ERC20 token from the
 * payer to the resource server. The approach results in the facilitator having no ability to direct fund anywhere but
 * the address specified by the resource server in paymentRequirements.
 * specs/schemes/exact/scheme_exact_evm.md
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@Repeatable(X402ExactSchemes.class)
@X402
public @interface X402ExactScheme {

    /**
     * The network to use (example: base-sepolia).
     *
     * @return the network
     */
    String network();

    /**
     * Payment amount required for access, as a string representation.
     * For cryptocurrencies with high precision, this allows representing amounts
     * in their smallest denomination (wei, satoshi, etc.) without precision loss.
     *
     * @return maximum amount required in the smallest denomination of the cryptocurrency
     */
    String maximumAmountRequired();

    /**
     * The description that will be displayed in the "accepts" response.
     *
     * @return the description
     */
    String description() default "";

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

}
