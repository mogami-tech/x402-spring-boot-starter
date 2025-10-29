package tech.mogami.spring.interceptor;

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
import tech.mogami.commons.api.facilitator.settle.SettleResponse;
import tech.mogami.commons.api.facilitator.verify.VerifyResponse;
import tech.mogami.commons.header.payment.PaymentPayload;
import tech.mogami.commons.header.payment.PaymentRequired;
import tech.mogami.commons.header.payment.PaymentRequirements;
import tech.mogami.commons.util.Base64Util;
import tech.mogami.commons.util.JsonUtil;
import tech.mogami.spring.annotation.X402PayUSDC;
import tech.mogami.spring.annotation.X402PaymentRequirements;
import tech.mogami.spring.factory.annotation.PayFactories;
import tech.mogami.spring.provider.facilitator.FacilitatorService;

import java.lang.annotation.Annotation;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.mogami.commons.constant.X402Constants.X402_PAYMENT_REQUIRED_MESSAGE;
import static tech.mogami.commons.constant.X402Constants.X402_X_PAYMENT_HEADER;
import static tech.mogami.commons.constant.X402Constants.X402_X_PAYMENT_HEADER_DECODED;
import static tech.mogami.commons.constant.X402Constants.X402_X_PAYMENT_RESPONSE;
import static tech.mogami.commons.constant.version.X402Versions.X402_SUPPORTED_VERSION_BY_MOGAMI;

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

    /** Pay factories. */
    private final PayFactories payFactories;

    /** Facilitator service. */
    private final FacilitatorService facilitatorService;

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request,
                             @NonNull final HttpServletResponse response,
                             @NonNull final Object handler) throws Exception {

        // We check if the handler is a HandlerMethod (spring method).
        if (handler instanceof HandlerMethod hm) {
            Set<Annotation> paymentRequirementsList = new LinkedHashSet<>();
            paymentRequirementsList.addAll(AnnotatedElementUtils.findMergedRepeatableAnnotations(hm.getMethod(), X402PaymentRequirements.class));
            paymentRequirementsList.addAll(AnnotatedElementUtils.findMergedRepeatableAnnotations(hm.getMethod(), X402PayUSDC.class));

            // We retrieve all schemes.
            if (!paymentRequirementsList.isEmpty()) {

                // x402 URL Called without payment =====================================================================
                if (request.getHeader(X402_X_PAYMENT_HEADER) == null) {
                    log.info("x402 URL Called without payment: {}", request.getRequestURL().toString());
                    response.setStatus(SC_PAYMENT_REQUIRED);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getWriter(), buildPaymentRequirementsBody(request, paymentRequirementsList));
                    return false; // We stop the chain.
                }

                // x402 URL Called with payment ========================================================================
                try {
                    // The payment is present, we decode it (base64) and add it to the response ========================
                    final String paymentHeaderString = new String(Base64.getMimeDecoder().decode(request.getHeader(X402_X_PAYMENT_HEADER)), UTF_8);
                    PaymentPayload paymentPayload = JsonUtil.fromJson(paymentHeaderString, PaymentPayload.class);
                    request.setAttribute(X402_X_PAYMENT_HEADER_DECODED, paymentPayload);
                    log.info("Payment received for url {}: {}", request.getRequestURL().toString(), paymentHeaderString);

                    final String nonce = paymentPayload.getNonce()
                            .orElseThrow(() -> new IllegalArgumentException("Nonce is required in the payment payload"));

                    // Now, we use the facilitator to check if the payment is isValid.
                    Annotation requirementsFound = paymentRequirementsList.stream()
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("No payment requirements found"));
                    final PaymentRequirements paymentRequirement = payFactories.buildRequirements(requirementsFound, request);

                    // We do the verification on the facilitator server ================================================
                    final VerifyResponse verifyResult = facilitatorService.verify(paymentPayload, paymentRequirement).block();
                    if (verifyResult == null) {
                        // Error calling the verify facilitator - null result ==========================================
                        log.error("Error calling /verify on facilitator - null result");
                        response.sendError(SC_BAD_REQUEST, "Error calling /verify on facilitator - null result");
                        return false;
                    }

                    // We have a result from the verification ==========================================================
                    log.info("Verify result: {}", verifyResult);
                    if (!verifyResult.isValid()) {
                        // Verification is invalid =====================================================================
                        log.error("Payment is invalid: {}", verifyResult);
                        response.setStatus(SC_PAYMENT_REQUIRED);
                        response.setContentType(APPLICATION_JSON_VALUE);
                        objectMapper.writeValue(response.getWriter(), buildPaymentRequirementsBody(request, paymentRequirementsList));
                        return false;
                    }

                    // Verification is valid ===========================================================================
                    log.info("Payment is valid: {}", verifyResult);

                    // Calling /settle and setting the response header =================================================
                    final SettleResponse settleResponse = facilitatorService.settle(paymentPayload, paymentRequirement).block();
                    if (settleResponse == null) {
                        log.error("Error calling the settle facilitator - null result");
                        response.sendError(SC_BAD_REQUEST, "Serveur error calling the facilitator");
                        return false;
                    }

                    // We have a valid result from the settlement ======================================================
                    log.info("Settle result: {}", settleResponse);
                    response.setHeader(X402_X_PAYMENT_RESPONSE, Base64Util.encode(JsonUtil.toJson(settleResponse)));
                    return true;

                } catch (IllegalArgumentException e) {
                    log.error("Error decoding payment header: {}", e.getMessage());
                    response.sendError(SC_BAD_REQUEST, "Invalid Base64");
                    return false;
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
                                                         final Set<Annotation> paymentRequirementsAnnotations) {
        return PaymentRequired.builder()
                .x402Version(X402_SUPPORTED_VERSION_BY_MOGAMI.version())
                .error(X402_PAYMENT_REQUIRED_MESSAGE)
                .accepts(paymentRequirementsAnnotations
                        .stream()
                        .map(paymentRequirement -> payFactories.buildRequirements(paymentRequirement, request))
                        .collect(Collectors.toCollection(LinkedList::new)))
                .build();
    }

}
