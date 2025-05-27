package tech.mogami.spring.autoconfigure.provider.facilitator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.mogami.commons.api.facilitator.settle.SettleRequest;
import tech.mogami.commons.api.facilitator.settle.SettleResponse;
import tech.mogami.commons.api.facilitator.supported.SupportedResponse;
import tech.mogami.commons.api.facilitator.verify.VerifyRequest;
import tech.mogami.commons.api.facilitator.verify.VerifyResponse;
import tech.mogami.commons.header.payment.PaymentPayload;
import tech.mogami.commons.header.payment.PaymentRequirements;
import tech.mogami.spring.autoconfigure.parameter.X402Parameters;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.mogami.commons.api.facilitator.FacilitatorRoutes.SETTLE_URL;
import static tech.mogami.commons.api.facilitator.FacilitatorRoutes.SUPPORTED_URL;
import static tech.mogami.commons.api.facilitator.FacilitatorRoutes.VERIFY_URL;

/**
 * {@link FacilitatorService} implementation.
 */
@Slf4j
@SuppressWarnings({"checkstyle:DesignForExtension", "unused"})
public class FacilitatorServiceImplementation implements FacilitatorService {

    /** Web client. */
    private final WebClient client;

    /**
     * Constructor for FacilitatorClient.
     *
     * @param facilitatorParameters facilitator parameters
     */
    public FacilitatorServiceImplementation(final X402Parameters.Facilitator facilitatorParameters) {
        this.client = WebClient.builder()
                .baseUrl(facilitatorParameters.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }

    @Override
    public Mono<SupportedResponse> supported() {
        return client.get()
                .uri(SUPPORTED_URL)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(SupportedResponse.class)
                .doOnError(WebClientResponseException.class, error ->
                        log.error("Facilitator /support error : '{}'", error.getResponseBodyAsString()));
    }

    @Override
    public Mono<VerifyResponse> verify(final PaymentPayload paymentPayload,
                                       final PaymentRequirements paymentRequirements) {
        VerifyRequest body = VerifyRequest.builder()
                .x402Version(paymentPayload.x402Version())
                .paymentPayload(paymentPayload)
                .paymentRequirements(paymentRequirements)
                .build();

        return client.post()
                .uri(VERIFY_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(VerifyResponse.class)
                .doOnError(WebClientResponseException.class, error ->
                        log.error("Facilitator /verify error: '{}'", error.getResponseBodyAsString()));
    }

    @Override
    public Mono<SettleResponse> settle(final PaymentPayload paymentPayload,
                                       final PaymentRequirements paymentRequirements) {
        SettleRequest body = SettleRequest.builder()
                .x402Version(paymentPayload.x402Version())
                .paymentPayload(paymentPayload)
                .paymentRequirements(paymentRequirements)
                .build();

        return client.post()
                .uri(SETTLE_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(SettleResponse.class)
                .doOnError(WebClientResponseException.class, error ->
                        log.error("Facilitator /settle error : '{}'", error.getResponseBodyAsString()));
    }

}
