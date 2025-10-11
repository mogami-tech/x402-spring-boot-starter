package tech.mogami.spring.provider.console;

import tech.mogami.commons.api.console.v1.EventRequest;

/**
 * ConsoleService - Service to handle Mogami x402 console interactions.
 */
public interface ConsoleService {

    /**
     * Log an event to the Mogami x402 console.
     *
     * @param event the event to log
     */
    void logEvent(EventRequest event);

}
