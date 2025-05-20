package tech.mogami.spring.autoconfigure.util.constants;

import lombok.experimental.UtilityClass;

/**
 * X402 constants.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class X402Constants {

    /** X402 version. */
    public static final int X402_SUPPORTED_VERSION = 1;

    /** X402 payment required header. */
    public static final String X402_X_PAYMENT_HEADER = "X-PAYMENT";

    /** X402 payment required header decoded - This is the added by the server to the response. */
    public static final String X402_X_PAYMENT_HEADER_DECODED = "X-PAYMENT-DECODED";

    /** X402 header containing the settlement Response as Base64 encoded JSON if the payment was executed successfully. */
    public static final String X402_X_PAYMENT_RESPONSE = "X-PAYMENT-RESPONSE";

    /** X402 payment required message. */
    public static final String X402_PAYMENT_REQUIRED_MESSAGE = "Payment required";

}
