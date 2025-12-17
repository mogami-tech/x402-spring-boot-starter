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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.mogami.commons.api.facilitator.settle.SettleResponse;
import tech.mogami.commons.api.facilitator.verify.VerifyResponse;
import tech.mogami.commons.payment.PaymentPayload;
import tech.mogami.commons.payment.PaymentRequired;
import tech.mogami.commons.payment.PaymentRequirements;
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
                    return402(request, response, null, null, paymentRequirementsList);
                    return false;
                }

                // x402 URL Called with payment ========================================================================
                try {

                    // =================================================================================================
                    // The payment is present, we decode it (base64) and add it to the response ========================
                    final String paymentHeaderString = new String(Base64.getMimeDecoder().decode(request.getHeader(X402_X_PAYMENT_HEADER)), UTF_8);
                    PaymentPayload paymentPayload = JsonUtil.fromJson(paymentHeaderString, PaymentPayload.class);
                    request.setAttribute(X402_X_PAYMENT_HEADER_DECODED, paymentPayload);
                    log.info("Payment received for url {}: {}", request.getRequestURL().toString(), paymentHeaderString);

                    // TODO Here, we check the payment against the fist requirement found. 
                    Annotation requirementsFound = paymentRequirementsList.stream()
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("No payment requirements found"));
                    final PaymentRequirements paymentRequirement = payFactories.buildRequirements(requirementsFound, request);

                    // =================================================================================================
                    // Calling /verify on the facilitator server========================================================
                    VerifyResponse verifyResponse;
                    try {
                        verifyResponse = facilitatorService.verify(paymentPayload, paymentRequirement).block();
                    } catch (WebClientResponseException e) {
                        // The call failed - the body should contain a verifyResponse.
                        String responseBody = e.getResponseBodyAsString(UTF_8);
                        log.error("Calling /verify failed: {} - {}", e.getStatusCode(), responseBody);
                        try {
                            verifyResponse = JsonUtil.fromJson(responseBody, VerifyResponse.class);
                        } catch (Exception ex) {
                            log.error("The result from /verify is not valid: {}", responseBody);
                            verifyResponse = VerifyResponse.builder()
                                    .isValid(false)
                                    .invalidReason("Reply error from calling /verify: " + responseBody)
                                    .build();
                        }
                        return402(request, response, verifyResponse, null, paymentRequirementsList);
                        return false;
                    }

                    // We have a result from the verification ==========================================================
                    log.info("Verify result: {}", verifyResponse);
                    if (verifyResponse == null || !verifyResponse.isValid()) {
                        // Payment is invalid
                        log.error("Payment is invalid: {}", verifyResponse);
                        return402(request, response, verifyResponse, null, paymentRequirementsList);
                        return false;
                    } else {
                        // Payment is valid
                        log.info("Payment is valid: {}", verifyResponse);
                    }

                    // =================================================================================================
                    // Calling /settle and setting the response header =================================================
                    SettleResponse settleResponse;
                    try {
                        settleResponse = facilitatorService.settle(paymentPayload, paymentRequirement).block();
                    } catch (WebClientResponseException e) {
                        // The call failed.
                        String responseBody = e.getResponseBodyAsString(UTF_8);
                        log.error("Calling /settle failed: {} - {}", e.getStatusCode(), responseBody);
                        try {
                            settleResponse = JsonUtil.fromJson(responseBody, SettleResponse.class);
                        } catch (Exception ex) {
                            log.error("The result from /settle is not valid: {}", responseBody);
                            settleResponse = SettleResponse.builder()
                                    .success(false)
                                    .errorReason("Reply error from calling /settle: " + responseBody)
                                    .build();
                        }
                        return402(request, response, null, settleResponse, paymentRequirementsList);
                        return false;
                    }

                    if (settleResponse == null || !settleResponse.success()) {
                        // Settlement failed
                        log.error("Payment settlement failed: {}", settleResponse);
                        return402(request, response, null, settleResponse, paymentRequirementsList);
                        return false;
                    } else {
                        // Payment settled ! we let the user access the resource.
                        log.info("Payment settled: {}", settleResponse);
                        response.setHeader(X402_X_PAYMENT_RESPONSE, Base64Util.encode(JsonUtil.toJson(settleResponse)));
                        return true;
                    }

                } catch (IllegalArgumentException e) {
                    log.error("Error decoding payment header: {}", e.getMessage());
                    final VerifyResponse verifyResponse = VerifyResponse.builder()
                            .isValid(false)
                            .invalidReason("Error decoding payment header: " + e.getMessage())
                            .build();
                    return402(request, response, verifyResponse, null, paymentRequirementsList);
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
     * Returns a 402 Payment Required response.
     *
     * @param request                        The HTTP request
     * @param response                       The HTTP response
     * @param verifyResponse                 The verify response
     * @param settleResponse                 The settle response
     * @param paymentRequirementsAnnotations The list of payment requirements annotations
     * @throws Exception If an error occurs
     */
    private void return402(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final VerifyResponse verifyResponse,
                           final SettleResponse settleResponse,
                           final Set<Annotation> paymentRequirementsAnnotations) throws Exception {

        // We search for an error message in the verifyResponse or settleResponse.
        String errorMessage = X402_PAYMENT_REQUIRED_MESSAGE;
        if (verifyResponse != null && verifyResponse.invalidReason() != null) {
            errorMessage = verifyResponse.invalidReason();
        }
        if (settleResponse != null && settleResponse.errorReason() != null) {
            errorMessage = settleResponse.errorReason();
        }

        // We build the payment required object.
        final PaymentRequired paymentRequired = PaymentRequired.builder()
                .x402Version(X402_SUPPORTED_VERSION_BY_MOGAMI.version())
                .error(errorMessage)
                .accepts(paymentRequirementsAnnotations
                        .stream()
                        .map(paymentRequirement -> payFactories.buildRequirements(paymentRequirement, request))
                        .collect(Collectors.toCollection(LinkedList::new)))
                .build();

        // We write the response.
        response.setStatus(SC_PAYMENT_REQUIRED);
        response.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), paymentRequired);
    }

}
