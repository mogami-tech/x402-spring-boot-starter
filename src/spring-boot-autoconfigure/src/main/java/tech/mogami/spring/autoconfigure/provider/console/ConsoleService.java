package tech.mogami.spring.autoconfigure.provider.console;

import tech.mogami.commons.api.console.v1.EventRequest;

/**
 * ConsoleService - Service to handle console interactions.
 */
public interface ConsoleService {

    /**
     * Log an event to the console.
     *
     * @param event the event to log
     */
    void logEvent(EventRequest event);

}
