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

    /** X402 payment required message. */
    public static final String X402_PAYMENT_REQUIRED_MESSAGE = "Payment required";

}
