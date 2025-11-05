package tech.mogami.spring.factory.annotation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tech.mogami.commons.constant.network.Network;
import tech.mogami.commons.constant.network.Networks;
import tech.mogami.spring.parameter.X402Parameters;
import tech.mogami.spring.pricing.AmountProviderResolver;

import java.lang.annotation.Annotation;

/**
 * Abstract implementation of PayFactory that provides a default behavior.
 *
 * @param <A> the annotation type handled by this factory
 */
@Slf4j
public abstract class AbstractPayFactory<A extends Annotation> implements PayFactory<A> {

    /** X402 parameters. */
    @Setter
    protected X402Parameters x402Parameters;

    /** Amount provider resolver. */
    @Setter
    protected AmountProviderResolver amountResolver;

    /**
     * Gets the network by name, defaulting to the one in the parameters if not specified.
     *
     * @param networkName the network name
     * @return the network
     * @throws IllegalArgumentException if the network is unsupported
     */
    protected Network getNetwork(final String networkName) {
        return Networks.findByName(StringUtils.firstNonBlank(networkName, x402Parameters.defaultNetwork()))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported network: " + networkName));
    }

    /**
     * Parses the output schema from a JSON string.
     *
     * @param outputSchema the JSON string
     * @return the parsed JsonNode, or null if invalid or blank
     */
    protected JsonNode parseOutputSchema(final String outputSchema) {
        if (StringUtils.isNotBlank(outputSchema)) {
            try {
                return new ObjectMapper().readTree(outputSchema);
            } catch (JsonProcessingException e) {
                log.error("Invalid outputSchema JSON: {}", outputSchema, e);
            }
        }
        return null;
    }

}
