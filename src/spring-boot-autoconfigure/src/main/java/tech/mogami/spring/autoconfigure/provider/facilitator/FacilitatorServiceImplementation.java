package tech.mogami.spring.autoconfigure.provider.facilitator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.mogami.spring.autoconfigure.dto.PaymentPayload;
import tech.mogami.spring.autoconfigure.dto.PaymentRequirements;
import tech.mogami.spring.autoconfigure.parameter.X402Parameters;
import tech.mogami.spring.autoconfigure.provider.facilitator.settle.SettleRequest;
import tech.mogami.spring.autoconfigure.provider.facilitator.settle.SettleResult;
import tech.mogami.spring.autoconfigure.provider.facilitator.supported.SupportedResponse;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyRequest;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyResponse;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorURLs.SETTLE_URL;
import static tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorURLs.SUPPORTED_URL;
import static tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorURLs.VERIFY_URL;

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
                        log.error("Facilitator support error : '{}'", error.getResponseBodyAsString()));
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
                        log.error("Facilitator verify error: '{}'", error.getResponseBodyAsString()));
    }

    @Override
    public Mono<SettleResult> settle(final PaymentPayload paymentPayload,
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
                .bodyToMono(SettleResult.class)
                .doOnError(WebClientResponseException.class, error ->
                        log.error("Facilitator settle error : '{}'", error.getResponseBodyAsString()));
    }

}
