package tech.mogami.spring.autoconfigure.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.mogami.spring.autoconfigure.parameter.X402Parameters;
import tech.mogami.spring.autoconfigure.schemes.PaymentRequired;
import tech.mogami.spring.autoconfigure.schemes.exact.ExactSchemePayment;
import tech.mogami.spring.autoconfigure.schemes.exact.ExactSchemePaymentRequirement;
import tech.mogami.spring.autoconfigure.schemes.exact.X402ExactScheme;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static tech.mogami.spring.autoconfigure.schemes.exact.ExactSchemeConstants.EXACT_SCHEME_NAME;
import static tech.mogami.spring.autoconfigure.util.constants.BlockchainConstants.DEFAULT_MAX_TIMEOUT_SECONDS;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_PAYMENT_REQUIRED_MESSAGE;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_SUPPORTED_VERSION;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_X_PAYMENT_HEADER;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_X_PAYMENT_HEADER_DECODED;

/**
 * Interceptor for x402.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:DesignForExtension")
public class X402Interceptor implements HandlerInterceptor {

    /** Object mapper. */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Mogami parameters. */
    private final X402Parameters x402Parameters;

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request,
                             @NonNull final HttpServletResponse response,
                             @NonNull final Object handler) throws Exception {

        // We check if the handler is a HandlerMethod (spring method).
        if (handler instanceof HandlerMethod hm) {
            // We retrieve all schemes.
            Set<X402ExactScheme> exactSchemes = AnnotatedElementUtils.findMergedRepeatableAnnotations(hm.getMethod(), X402ExactScheme.class);
            if (!exactSchemes.isEmpty()) {

                // =====================================================================================================
                // The method is annotated with @X402.

                // We check if the payment is present or not.
                if (request.getHeader(X402_X_PAYMENT_HEADER) == null) {
                    response.setStatus(SC_PAYMENT_REQUIRED);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getWriter(), buildPaymentRequiredBody(request, exactSchemes));
                    return false; // We stop the chain.
                } else {
                    try {
                        // The payment is present, we decode it (base64).
                        String paymentHeaderString = new String(
                                Base64.getMimeDecoder()
                                        .decode(request.getHeader(X402_X_PAYMENT_HEADER)), UTF_8);
                        System.out.println("=> " + paymentHeaderString);

                        // We transform it as an object and also add itn decoded, to the response.
                        ExactSchemePayment exactSchemePayment = ExactSchemePayment.fromHeader(paymentHeaderString, objectMapper);
                        request.setAttribute(X402_X_PAYMENT_HEADER_DECODED, exactSchemePayment);

                        log.info("Payment received: {}", exactSchemePayment);
                        return true;
                    } catch (IllegalArgumentException e) {
                        response.sendError(SC_BAD_REQUEST, "Base64 invalide");
                        return false;
                    }
                }
                // =====================================================================================================

            } else {
                // Our annotation is not present, so we skip it.
                return true;
            }
        } else {
            // The handler is not a HandlerMethod (spring method), so we skip it.
            return true;
        }
    }

    /**
     * Builds the body for the payment required response.
     *
     * @param request      request
     * @param exactSchemes the exact schemes
     * @return the body to return
     */
    private PaymentRequired buildPaymentRequiredBody(final HttpServletRequest request,
                                                     final Set<X402ExactScheme> exactSchemes) {
        // List of all accepts
        List<ExactSchemePaymentRequirement> exactSchemePaymentRequirements = new LinkedList<>();

        // We build the "accepts" part of the response with X402ExactScheme.
        exactSchemes.forEach(x402ExactScheme ->
                exactSchemePaymentRequirements.add(ExactSchemePaymentRequirement.builder()
                        .scheme(EXACT_SCHEME_NAME)
                        .network(x402ExactScheme.network())
                        .maxAmountRequired(x402ExactScheme.maximumAmountRequired())
                        .resource(request.getRequestURL().toString())
                        .description(x402ExactScheme.description())
                        .mimeType("")   // TODO Manage mime type
                        .payTo(x402ExactScheme.payTo())
                        .maxTimeoutSeconds(DEFAULT_MAX_TIMEOUT_SECONDS)
                        .asset(x402ExactScheme.asset())
                        .extra(new HashMap<>())
                        .build()));

        // Return the payment required body.
        return PaymentRequired.builder()
                .x402Version(X402_SUPPORTED_VERSION)
                .error(X402_PAYMENT_REQUIRED_MESSAGE)
                .accepts(exactSchemePaymentRequirements)
                .build();
    }

}
