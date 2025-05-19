package tech.mogami.spring.autoconfigure.provider.facilitator;

import reactor.core.publisher.Mono;
import tech.mogami.spring.autoconfigure.dto.PaymentPayload;
import tech.mogami.spring.autoconfigure.dto.PaymentRequirements;
import tech.mogami.spring.autoconfigure.provider.facilitator.supported.SupportedResponse;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyResponse;

/**
 * FacilitatorClient is a client for the external facilitator.
 */
public interface FacilitatorService {

    /**
     * Retrieve the supported payment methods from the facilitator service.
     *
     * @return a Mono of SupportedResponse
     */
    Mono<SupportedResponse> supported();

    /**
     * Verify the payment with the facilitator service.
     *
     * @param paymentPayload      payment payload received from the user
     * @param paymentRequirements payment requirements
     * @return status
     */
    Mono<VerifyResponse> verify(PaymentPayload paymentPayload,
                                PaymentRequirements paymentRequirements);

}
