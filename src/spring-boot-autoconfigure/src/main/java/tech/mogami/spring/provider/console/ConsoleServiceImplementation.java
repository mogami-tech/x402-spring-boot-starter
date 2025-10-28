package tech.mogami.spring.provider.console;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import tech.mogami.commons.api.console.v1.EventRequest;

import static tech.mogami.commons.api.console.ConsoleApiEndpoints.MOGAMI_X402_CONSOLE_API_BASE_URL;

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
                .baseUrl(MOGAMI_X402_CONSOLE_API_BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }

    @Override
    public final void logEvent(final EventRequest event) {
        log.debug("Received event: {}", event);
    }

}
