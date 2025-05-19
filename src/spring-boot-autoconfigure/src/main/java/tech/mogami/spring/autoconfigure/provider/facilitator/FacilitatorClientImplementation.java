package tech.mogami.spring.autoconfigure.provider.facilitator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.mogami.spring.autoconfigure.dto.PaymentPayload;
import tech.mogami.spring.autoconfigure.dto.PaymentRequirements;
import tech.mogami.spring.autoconfigure.parameter.X402Parameters;
import tech.mogami.spring.autoconfigure.provider.facilitator.supported.SupportedResponse;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyRequest;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.VerifyResponse;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorURLs.SUPPORTED_URL;
import static tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorURLs.VERIFY_URL;

/**
 * {@link FacilitatorClient} implementation.
 */
@Slf4j
@SuppressWarnings({"checkstyle:DesignForExtension", "unused"})
public class FacilitatorClientImplementation implements FacilitatorClient {

    /** Web client. */
    private final WebClient client;

    /**
     * Constructor for FacilitatorClient.
     *
     * @param facilitatorParameters facilitator parameters
     */
    public FacilitatorClientImplementation(final X402Parameters.Facilitator facilitatorParameters) {
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
                .bodyToMono(SupportedResponse.class);
    }

    @Override
    public Mono<VerifyResponse> verify(final PaymentPayload paymentPayload,
                                       final PaymentRequirements paymentRequirements) {
        VerifyRequest body = VerifyRequest.builder()
                .x402Version(paymentPayload.x402Version())
                .paymentPayload(paymentPayload)
                .paymentRequirements(paymentRequirements)
                .build();
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("→ Requête JSON: " + body.toJSON());

        return client.post()
                .uri(VERIFY_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(VerifyResponse.class)
                .doOnError(WebClientResponseException.class, error ->
                        log.error("Facilitator body: '{}'", error.getResponseBodyAsString()));
    }

}
