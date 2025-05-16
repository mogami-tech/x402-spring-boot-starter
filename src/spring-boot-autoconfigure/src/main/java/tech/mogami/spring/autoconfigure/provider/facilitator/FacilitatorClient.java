package tech.mogami.spring.autoconfigure.provider.facilitator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.mogami.spring.autoconfigure.parameters.X402Parameters;
import tech.mogami.spring.autoconfigure.payload.PaymentHeader;
import tech.mogami.spring.autoconfigure.provider.facilitator.supported.SupportedResponse;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.PaymentRequirements;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyRequest;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyResponse;

import java.util.Base64;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * FacilitatorClient is a client for the facilitator service.
 */
@Slf4j
public class FacilitatorClient {

    /** X402 version. */
    private final int x402Version;

    /** Web client. */
    private final WebClient client;

    /**
     * Constructor for FacilitatorClient.
     *
     * @param newX402Version        the version of the x402 protocol
     * @param facilitatorParameters facilitator parameters
     */
    public FacilitatorClient(final int newX402Version,  // TODO Is it useful?
                             final X402Parameters.Facilitator facilitatorParameters) {
        this.x402Version = newX402Version;
        this.client = WebClient.builder()
                .baseUrl(facilitatorParameters.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }

    /**
     * Retrieve the supported payment methods from the facilitator service.
     *
     * @return a Mono of SupportedResponse
     */
    public Mono<SupportedResponse> supported() {
        return client.get()
                .uri("/supported")  // TODO Use constant
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(SupportedResponse.class);
    }

    /**
     * Verify the payment with the facilitator service.
     *
     * @param paymentHeader       payment received from the user
     * @param paymentRequirements payment requirements
     * @return status
     */
    public Mono<VerifyResponse> verify(final PaymentHeader paymentHeader,
                                       final PaymentRequirements paymentRequirements) throws JsonProcessingException {
        String encodedHeader = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(new ObjectMapper().writeValueAsBytes(paymentHeader));

        VerifyRequest body = VerifyRequest.builder()
                .x402Version(x402Version)
                .paymentHeader(encodedHeader)
                .paymentRequirements(paymentRequirements)
                .build();

        return client.post()
                .uri("/verify")
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(VerifyResponse.class)
                .doOnError(WebClientResponseException.class, err ->
                        log.error("Facilitator body: '{}'", err.getResponseBodyAsString()));
    }

}
