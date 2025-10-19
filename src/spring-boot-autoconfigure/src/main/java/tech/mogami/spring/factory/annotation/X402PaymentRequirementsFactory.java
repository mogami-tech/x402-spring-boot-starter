package tech.mogami.spring.factory.annotation;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import tech.mogami.commons.constant.network.Network;
import tech.mogami.commons.constant.network.Networks;
import tech.mogami.commons.header.payment.PaymentRequirements;
import tech.mogami.spring.annotation.X402PaymentRequirements;

import java.util.Arrays;
import java.util.stream.Collectors;

import static tech.mogami.commons.constant.X402Constants.X402_DEFAULT_PAYMENT_TIMEOUT_SECONDS;

/**
 * X402 Payment Requirements Factory.
 */
public class X402PaymentRequirementsFactory extends AbstractPayFactory<X402PaymentRequirements> {

    @Override
    public final PaymentRequirements buildRequirements(final X402PaymentRequirements annotation, final HttpServletRequest request) {
        // We check the network to be used, defaulting to the one in the parameters if not specified
        Network network = Networks.findByName(StringUtils.firstNonBlank(annotation.network(), x402Parameters.defaultNetwork()))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported network: " + annotation.network()));

        return PaymentRequirements.builder()
                .scheme(annotation.scheme())
                .network(network.name())
                .maxAmountRequired(annotation.maximumAmountRequired())
                .resource(request.getRequestURL().toString())
                .description(annotation.description())
                .mimeType("")
                .payTo(StringUtils.firstNonBlank(annotation.payTo(), x402Parameters.defaultPayTo()))
                .maxTimeoutSeconds(X402_DEFAULT_PAYMENT_TIMEOUT_SECONDS)
                .asset(StringUtils.firstNonBlank(annotation.asset(), network.usdc().contractAddress()))
                .extra(Arrays.stream(annotation.extra())
                        .collect(Collectors.toMap(
                                X402PaymentRequirements.ExtraEntry::key,
                                X402PaymentRequirements.ExtraEntry::value)))
                .build();
    }
}
