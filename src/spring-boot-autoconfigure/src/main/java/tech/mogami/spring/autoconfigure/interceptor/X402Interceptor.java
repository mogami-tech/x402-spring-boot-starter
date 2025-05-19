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
import tech.mogami.spring.autoconfigure.annotation.X402PaymentRequirement;
import tech.mogami.spring.autoconfigure.dto.PaymentPayload;
import tech.mogami.spring.autoconfigure.dto.PaymentRequired;
import tech.mogami.spring.autoconfigure.dto.PaymentRequirements;
import tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorService;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyResponse;

import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
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

    /** Facilitator service. */
    private final FacilitatorService facilitatorService;

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request,
                             @NonNull final HttpServletResponse response,
                             @NonNull final Object handler) throws Exception {

        // We check if the handler is a HandlerMethod (spring method).
        if (handler instanceof HandlerMethod hm) {
            // We retrieve all schemes.
            Set<X402PaymentRequirement> paymentRequirementsList = AnnotatedElementUtils.findMergedRepeatableAnnotations(hm.getMethod(), X402PaymentRequirement.class);
            if (!paymentRequirementsList.isEmpty()) {

                // =====================================================================================================
                // The method is annotated with @X402.

                // We check if the payment is present or not.
                if (request.getHeader(X402_X_PAYMENT_HEADER) == null) {
                    // The payment is not present, we build the response by listing the payment requirements.
                    response.setStatus(SC_PAYMENT_REQUIRED);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getWriter(), buildPaymentRequirementsBody(request, paymentRequirementsList));
                    return false; // We stop the chain.
                } else {
                    try {
                        // The payment is present, we decode it (base64) and add it to the response.
                        final String paymentHeaderString = new String(Base64.getMimeDecoder().decode(request.getHeader(X402_X_PAYMENT_HEADER)), UTF_8);
                        PaymentPayload paymentPayload = PaymentPayload.loadFromJSONString(paymentHeaderString, objectMapper);
                        request.setAttribute(X402_X_PAYMENT_HEADER_DECODED, paymentPayload);
                        log.info("Payment received: {}", paymentPayload);

                        // Now, we use the facilitator to check if the payment is isValid.
                        X402PaymentRequirement test = paymentRequirementsList.stream()
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("No payment requirements found"));
                        final PaymentRequirements paymentRequirement = buildPaymentRequirements(request, test);

                        final VerifyResponse verifyResult = facilitatorService.verify(paymentPayload, paymentRequirement).block();
                        if (verifyResult == null) {
                            response.sendError(SC_BAD_REQUEST, "Serveur error calling the facilitator");
                            return false;
                        }
                        System.out.println("verifyResult = " + verifyResult);
                        if (verifyResult.isValid()) {
                            // If the Verification Response is isValid, the resource server performs the work to fulfill
                            // the request.
                            log.info("Payment is isValid: {}", verifyResult);
                            return true;
                        } else {
                            // If the Verification Response is invalid, the resource server returns a 402-Payment
                            // Required status and a Payment Required Response JSON object in the response body.
                            response.setStatus(SC_PAYMENT_REQUIRED);
                            response.setContentType(APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getWriter(), buildPaymentRequirementsBody(request, paymentRequirementsList));
                            return false;
                        }
                    } catch (IllegalArgumentException e) {
                        response.sendError(SC_BAD_REQUEST, "Invalid Base64");
                        return false;
                    }
                }
                // =====================================================================================================

            } else {
                // Our annotation is not present, so we skip it, it's free!
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
     * @param request                        The HTTP request
     * @param paymentRequirementsAnnotations The list of payment requirements annotations
     * @return The payment required body
     */
    private PaymentRequired buildPaymentRequirementsBody(final HttpServletRequest request,
                                                         final Set<X402PaymentRequirement> paymentRequirementsAnnotations) {
        return PaymentRequired.builder()
                .x402Version(X402_SUPPORTED_VERSION)
                .error(X402_PAYMENT_REQUIRED_MESSAGE)
                .accepts(paymentRequirementsAnnotations
                        .stream()
                        .map(paymentRequirement -> buildPaymentRequirements(request, paymentRequirement))
                        .collect(Collectors.toCollection(LinkedList::new)))
                .build();
    }

    /**
     * Builds a payment requirement.
     *
     * @param request                       The HTTP request
     * @param paymentRequirementsAnnotation The payment requirements annotation
     * @return The payment required body
     */
    private PaymentRequirements buildPaymentRequirements(final HttpServletRequest request,
                                                         final X402PaymentRequirement paymentRequirementsAnnotation) {
        return PaymentRequirements.builder()
                .scheme(paymentRequirementsAnnotation.scheme())
                .network(paymentRequirementsAnnotation.network())
                .maxAmountRequired(paymentRequirementsAnnotation.maximumAmountRequired())
                .resource(request.getRequestURL().toString())
                .description(paymentRequirementsAnnotation.description())
                .mimeType("")
                .payTo(paymentRequirementsAnnotation.payTo())
                .maxTimeoutSeconds(DEFAULT_MAX_TIMEOUT_SECONDS)
                .asset(paymentRequirementsAnnotation.asset())
                .extra(Arrays.stream(paymentRequirementsAnnotation.extra())
                        .collect(Collectors.toMap(
                                X402PaymentRequirement.ExtraEntry::key,
                                X402PaymentRequirement.ExtraEntry::value)))
                .build();
    }

}
