package tech.mogami.spring.autoconfigure.provider.facilitator;

import reactor.core.publisher.Mono;
import tech.mogami.commons.api.facilitator.settle.SettleResponse;
import tech.mogami.commons.api.facilitator.supported.SupportedResponse;
import tech.mogami.commons.api.facilitator.verify.VerifyResponse;
import tech.mogami.commons.header.payment.PaymentPayload;
import tech.mogami.commons.header.payment.PaymentRequirements;

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

    /**
     * Settle the payment with the facilitator service.
     *
     * @param paymentPayload      payment payload received from the user
     * @param paymentRequirements payment requirements
     * @return status
     */
    Mono<SettleResponse> settle(PaymentPayload paymentPayload,
                                PaymentRequirements paymentRequirements);


}
