package tech.mogami.spring.autoconfigure.provider.facilitator;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * FacilitatorClient is a client for the facilitator service.
 */
public class FacilitatorClient {

    /** Web client. */
    private final WebClient client;

    /** X402 version. */
    private final int x402Version;

    /**
     * Constructor for FacilitatorClient.
     *
     * @param baseUrl        the base URL of the facilitator service
     * @param newX402Version the version of the x402 protocol
     */
    public FacilitatorClient(final String baseUrl, final int newX402Version) {
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
        this.x402Version = newX402Version;
    }

    /**
     * Retrieve the supported payment methods from the facilitator service.
     *
     * @return a Mono of SupportedResponse
     */
    public Mono<SupportedResponse> supported() {
        return client.get()
                .uri("/supported")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(SupportedResponse.class)
                .doOnNext(System.out::println);
    }

}
