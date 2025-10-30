package tech.mogami.spring.factory.annotation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tech.mogami.commons.constant.network.Network;
import tech.mogami.commons.constant.network.Networks;
import tech.mogami.commons.header.payment.PaymentRequirements;
import tech.mogami.spring.annotation.X402PayUSDC;

import java.util.Map;

import static tech.mogami.commons.header.payment.schemes.exact.ExactSchemeConstants.EXACT_SCHEME_PARAMETER_NAME;
import static tech.mogami.commons.header.payment.schemes.exact.ExactSchemeConstants.EXACT_SCHEME_PARAMETER_VERSION;

/**
 * X402 Pay USDC Factory.
 */
@Slf4j
public class X402PayUSDCFactory extends AbstractPayFactory<X402PayUSDC> {

    @Override
    public final PaymentRequirements buildRequirements(final X402PayUSDC annotation, final HttpServletRequest request) {
        // We check the network to be used, defaulting to the one in the parameters if not specified
        Network network = Networks.findByName(StringUtils.firstNonBlank(annotation.network(), x402Parameters.defaultNetwork()))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported network: " + annotation.network()));

        // Deal with outputSchema being JSON
        String json = annotation.outputSchema();
        JsonNode outputSchema = null;
        if (!json.isBlank()) {
            try {
                outputSchema = new ObjectMapper().readTree(json);
            } catch (JsonProcessingException e) {
                log.error("Invalid outputSchema JSON: {}", json, e);
            }
        }

        return PaymentRequirements.builder()
                .scheme(annotation.scheme())
                .network(network.name())
                .maxAmountRequired(network.usdc().toAtomic(annotation.amount()).toPlainString())
                .resource(request.getRequestURL().toString())
                .description(annotation.description())
                .mimeType(annotation.mimeType())
                .outputSchema(outputSchema)
                .payTo(StringUtils.firstNonBlank(annotation.payTo(), x402Parameters.defaultPayTo()))
                .maxTimeoutSeconds(annotation.maximumTimeoutSeconds())
                .mimeType(annotation.mimeType())
                .asset(network.usdc().contractAddress())
                .extra(Map.of(EXACT_SCHEME_PARAMETER_NAME, network.usdc().displayName()))
                .extra(Map.of(EXACT_SCHEME_PARAMETER_VERSION, "2"))
                .build();
    }

}
