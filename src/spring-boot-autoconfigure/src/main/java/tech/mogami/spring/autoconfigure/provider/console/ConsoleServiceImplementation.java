package tech.mogami.spring.autoconfigure.provider.console;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import tech.mogami.commons.api.console.v1.EventRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static tech.mogami.commons.api.console.ConsoleApiEndpoints.API_BASE_URL;
import static tech.mogami.commons.api.console.ConsoleApiEndpoints.V1.EVENTS_URL;

/**
 * {@link ConsoleService} implementation.
 */
@Slf4j
@Service
@SuppressWarnings({"checkstyle:DesignForExtension", "unused"})
public class ConsoleServiceImplementation implements ConsoleService {

    /** Web client. */
    private final WebClient client;

    /**
     * Default constructor.
     */
    public ConsoleServiceImplementation() {
        this.client = WebClient.builder()
                // TODO Manage development and production URLs
                .baseUrl(API_BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }

    @Override
    public final void logEvent(final EventRequest event) {
        client.post()
                .uri(EVENTS_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(event)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(eventId ->
                        log.info("{}: Event logged successfully for nonce: {} with event id: {}",
                                event.type(), event.nonce(), eventId)
                )
                .doOnError(e ->
                        log.error("{}: Failed to log event: {}", event.type(), e.getMessage(), e)
                )
                .subscribe();
    }


}
