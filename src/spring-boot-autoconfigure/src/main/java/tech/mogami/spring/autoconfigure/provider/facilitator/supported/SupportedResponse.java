package tech.mogami.spring.autoconfigure.provider.facilitator.supported;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Facilitator → client payload for GET /supported.
 *
 * @param kinds list of supported (scheme, network) pairs
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SupportedResponse(
        List<SupportedKind> kinds
) {
}


