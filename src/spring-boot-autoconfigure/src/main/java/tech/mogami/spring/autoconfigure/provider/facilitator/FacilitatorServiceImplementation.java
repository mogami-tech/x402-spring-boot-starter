package tech.mogami.spring.autoconfigure.provider.facilitator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.mogami.commons.api.console.v1.EventRequest;
import tech.mogami.commons.api.facilitator.settle.SettleRequest;
import tech.mogami.commons.api.facilitator.settle.SettleResponse;
import tech.mogami.commons.api.facilitator.supported.SupportedResponse;
import tech.mogami.commons.api.facilitator.verify.VerifyRequest;
import tech.mogami.commons.api.facilitator.verify.VerifyResponse;
import tech.mogami.commons.header.payment.PaymentPayload;
import tech.mogami.commons.header.payment.PaymentRequirements;
import tech.mogami.commons.util.JsonUtil;
import tech.mogami.spring.autoconfigure.parameter.X402Parameters;
import tech.mogami.spring.autoconfigure.provider.console.ConsoleService;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.mogami.commons.api.console.EventType.X402_SERVER_PAYMENT_SETTLE_REQUEST;
import static tech.mogami.commons.api.console.EventType.X402_SERVER_PAYMENT_SETTLE_RESPONSE;
import static tech.mogami.commons.api.console.EventType.X402_SERVER_PAYMENT_VERIFY_REQUEST;
import static tech.mogami.commons.api.console.EventType.X402_SERVER_PAYMENT_VERIFY_RESPONSE;
import static tech.mogami.commons.api.facilitator.FacilitatorApiEndpoints.SETTLE_URL;
import static tech.mogami.commons.api.facilitator.FacilitatorApiEndpoints.SUPPORTED_URL;
import static tech.mogami.commons.api.facilitator.FacilitatorApiEndpoints.VERIFY_URL;

/**
 * {@link FacilitatorService} implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"checkstyle:DesignForExtension", "unused"})
public class FacilitatorServiceImplementation implements FacilitatorService {

    /** X402 parameters. */
    private final X402Parameters x402Parameters;

    /** Console service. */
    private final ConsoleService consoleService;

    /** Web client. */
    private WebClient client;

    /**
     * Building web client.
     */
    @PostConstruct
    public void init() {
        this.client = WebClient.builder()
                .baseUrl(x402Parameters.facilitator().baseUrl())
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
                .doOnNext(response -> log.info("Facilitator /support response: '{}'", JsonUtil.toJson(response)))
                .doOnError(WebClientResponseException.class, error ->
                        log.error("Facilitator /support error: '{}'", error.getResponseBodyAsString()));
    }

    @Override
    public Mono<VerifyResponse> verify(final PaymentPayload paymentPayload,
                                       final PaymentRequirements paymentRequirements) {
        VerifyRequest verifyRequest = VerifyRequest.builder()
                .x402Version(paymentPayload.x402Version())
                .paymentPayload(paymentPayload)
                .paymentRequirements(paymentRequirements)
                .build();
        log.info("Facilitator /verify request: '{}'", JsonUtil.toJson(verifyRequest));

        final String nonce = paymentPayload.getNonce()
                .orElseThrow(() -> new IllegalArgumentException("Nonce is required in the payment payload"));

        consoleService.logEvent(EventRequest.builder()
                .type(X402_SERVER_PAYMENT_VERIFY_REQUEST)
                .nonce(nonce)
                .payload(JsonUtil.toPrettyJson(verifyRequest))
                .build());

        return client.post()
                .uri(VERIFY_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(verifyRequest)
                .retrieve()
                .bodyToMono(VerifyResponse.class)
                .doOnNext(response -> {
                    log.info("Facilitator /verify response: '{}'", JsonUtil.toJson(response));

                    // Fire-and-forget
                    consoleService.logEvent(EventRequest.builder()
                            .type(X402_SERVER_PAYMENT_VERIFY_RESPONSE)
                            .nonce(nonce)
                            .payload(JsonUtil.toPrettyJson(verifyRequest))
                            .errorMessage(response.invalidReason())
                            .build());
                })
                .doOnError(WebClientResponseException.class, error -> {
                    log.error("Facilitator /verify error: '{}'", error.getResponseBodyAsString());

                    // Fire-and-forget
                    consoleService.logEvent(EventRequest.builder()
                            .type(X402_SERVER_PAYMENT_VERIFY_RESPONSE)
                            .nonce(nonce)
                            .payload(error.getResponseBodyAsString())
                            .errorMessage(error.getResponseBodyAsString())
                            .build());
                });
    }

    @Override
    public Mono<SettleResponse> settle(final PaymentPayload paymentPayload,
                                       final PaymentRequirements paymentRequirements) {
        SettleRequest settleRequest = SettleRequest.builder()
                .x402Version(paymentPayload.x402Version())
                .paymentPayload(paymentPayload)
                .paymentRequirements(paymentRequirements)
                .build();
        log.info("Facilitator /settle request: '{}'", JsonUtil.toJson(settleRequest));

        final String nonce = paymentPayload.getNonce()
                .orElseThrow(() -> new IllegalArgumentException("Nonce is required in the payment payload"));

        consoleService.logEvent(EventRequest.builder()
                .type(X402_SERVER_PAYMENT_SETTLE_REQUEST)
                .nonce(nonce)
                .payload(JsonUtil.toPrettyJson(settleRequest))
                .build());

        return client.post()
                .uri(SETTLE_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(settleRequest)
                .retrieve()
                .bodyToMono(SettleResponse.class)
                .doOnNext(response -> {
                    log.info("Facilitator /settle response: '{}'", JsonUtil.toJson(response));

                    // Fire-and-forget
                    consoleService.logEvent(EventRequest.builder()
                            .type(X402_SERVER_PAYMENT_SETTLE_RESPONSE)
                            .nonce(nonce)
                            .payload(JsonUtil.toPrettyJson(settleRequest))
                            .errorMessage(response.errorReason())
                            .build());
                })
                .doOnError(WebClientResponseException.class, error -> {
                    log.error("Facilitator /settle error: '{}'", error.getResponseBodyAsString());

                    // Fire-and-forget
                    consoleService.logEvent(EventRequest.builder()
                            .type(X402_SERVER_PAYMENT_SETTLE_RESPONSE)
                            .nonce(nonce)
                            .payload(error.getResponseBodyAsString())
                            .errorMessage(error.getResponseBodyAsString())
                            .build());
                });
    }

}
