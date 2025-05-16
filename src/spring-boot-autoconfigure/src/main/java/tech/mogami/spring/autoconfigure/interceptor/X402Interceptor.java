package tech.mogami.spring.autoconfigure.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.mogami.spring.autoconfigure.annotation.X402;
import tech.mogami.spring.autoconfigure.parameters.X402Parameters;
import tech.mogami.spring.autoconfigure.payload.Accept;
import tech.mogami.spring.autoconfigure.payload.PaymentHeader;
import tech.mogami.spring.autoconfigure.payload.PaymentRequiredBody;

import java.util.HashMap;

import static jakarta.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static tech.mogami.spring.autoconfigure.util.constants.BlockchainConstants.DEFAULT_MAX_TIMEOUT_SECONDS;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_PAYMENT_REQUIRED_MESSAGE;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_SUPPORTED_VERSION;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_X_PAYMENT_HEADER;

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
            // We check if we have the annotation.
            final X402 annotation = AnnotationUtils.findAnnotation(hm.getMethod(), X402.class);
            if (annotation != null) {

                // =====================================================================================================
                // The method is annotated with @X402.

                // We check if the payment is present or not.
                if (request.getHeader(X402_X_PAYMENT_HEADER) == null) {
                    response.setStatus(SC_PAYMENT_REQUIRED);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getWriter(), buildPaymentRequiredBody(request, annotation));
                    return false; // We stop the chain.
                } else {
                    // The payment is present, we keep it.
                    PaymentHeader paymentHeader = PaymentHeader.fromHeader(request.getHeader(X402_X_PAYMENT_HEADER), objectMapper);
                    request.setAttribute(PaymentHeader.class.getName(), paymentHeader);
                    log.info("Payment received: {}", paymentHeader);
                    // TODO Implement payment verification.
                    return true;
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
     * @param request    request
     * @param annotation annotation
     * @return the body to return
     */
    private PaymentRequiredBody buildPaymentRequiredBody(final HttpServletRequest request,
                                                         final X402 annotation) {
        return PaymentRequiredBody.builder()
                .x402Version(X402_SUPPORTED_VERSION)
                .error(X402_PAYMENT_REQUIRED_MESSAGE)
                .accept(Accept.builder()
                        .scheme("exact")        // TODO Replace with a constant after knowing what is it ?
                        .network(annotation.network())
                        .maxAmountRequired(annotation.maximumAmountRequired())
                        .resource(request.getRequestURL().toString())
                        .description("")        // TODO Add description in the annotation.
                        .mimeType("")           // TODO Manage mime type but I don't know how yet.
                        .payTo(annotation.payTo())
                        .maxTimeoutSeconds(DEFAULT_MAX_TIMEOUT_SECONDS)
                        .asset(annotation.asset())
                        .extra(new HashMap<>())
                        .build())
                .build();
    }

}
