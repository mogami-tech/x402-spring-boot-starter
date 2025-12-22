package tech.mogami.spring.test.integration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tech.mogami.commons.payment.PaymentRequired;
import tech.mogami.java.client.X402V2Client;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static jakarta.servlet.http.HttpServletResponse.SC_PAYMENT_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static tech.mogami.commons.constant.network.Networks.BASE_SEPOLIA;
import static tech.mogami.commons.constant.network.contract.BaseContracts.BASE_SEPOLIA_USDC_CONTRACT;
import static tech.mogami.commons.constant.version.X402Versions.X402_SUPPORTED_VERSION_BY_MOGAMI;
import static tech.mogami.commons.payment.schemes.Schemes.EXACT_SCHEME;
import static tech.mogami.commons.payment.schemes.exact.ExactSchemeConstants.EXACT_SCHEME_PARAMETER_NAME;
import static tech.mogami.commons.payment.schemes.exact.ExactSchemeConstants.EXACT_SCHEME_PARAMETER_VERSION;

@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "x402.facilitator.base-url=https://x402.org/facilitator"
)
@DisplayName("Payment integration tests")
public class PaymentTest {

    @LocalServerPort
    int port;

    static Stream<String> protectedUrls() {
        return Stream.of(
                //"https://www.x402.org/protected"
                "http://localhost:[PORT]/protected"
        );
    }

    /** OkHttpClient instance for making HTTP requests */
    private static final OkHttpClient CLIENT = new OkHttpClient();

    @MethodSource("protectedUrls")
    @ParameterizedTest(name = "Paywall enforced on {0}")
    @DisplayName("Paywall on https://www.x402.org/protected and localhost")
    void paywall(final String url) {
        final String finalUrl = url.replace("[PORT]", Integer.toString(port));

        // Calling protected url without payment =======================================================================
        try (Response initialResponse = CLIENT.newCall(new Request.Builder().url(finalUrl).get().build()).execute()) {

            if (initialResponse.code() == SC_PAYMENT_REQUIRED) {

                // Extracting the payments requirements from the header ================================================
                Optional<PaymentRequired> requirements = X402V2Client.fetchPaymentRequired(getHeaders(initialResponse));
                assertThat(requirements).isNotEmpty().get()
                        .satisfies(paymentRequired -> {
                            assertThat(paymentRequired.getVersion()).isPresent();
                            assertThat(paymentRequired.getVersion().get()).isEqualTo(X402_SUPPORTED_VERSION_BY_MOGAMI);
                            assertThat(paymentRequired.error()).isEqualTo("Payment required");
                            assertThat(paymentRequired.resource())
                                    .satisfies(paymentResource -> {
                                        assertThat(paymentResource.url()).contains("https://www.x402.org/protected");
                                        assertThat(paymentResource.description()).contains("Access to protected content");
                                        assertThat(paymentResource.mimeType()).isBlank();
                                    });
                            assertThat(paymentRequired.accepts())
                                    .hasSize(2)
                                    .satisfies(accepts -> {
                                        assertThat(accepts.getFirst())
                                                .satisfies(accept -> {
                                                    assertThat(accept.scheme()).isEqualTo(EXACT_SCHEME.name());
                                                    assertThat(accept.network()).isEqualTo(BASE_SEPOLIA.networkId());
                                                    assertThat(accept.amount()).isEqualTo("10000");
                                                    assertThat(accept.asset()).isEqualTo(BASE_SEPOLIA_USDC_CONTRACT);
                                                    assertThat(accept.payTo()).isEqualTo("0x209693Bc6afc0C5328bA36FaF03C514EF312287C");
                                                    assertThat(accept.maxTimeoutSeconds()).isEqualTo(300);
                                                    assertThat(accept.getExtra(EXACT_SCHEME_PARAMETER_NAME))
                                                            .isPresent()
                                                            .get()
                                                            .isEqualTo("USDC");
                                                    assertThat(accept.getExtra(EXACT_SCHEME_PARAMETER_VERSION))
                                                            .isPresent()
                                                            .get()
                                                            .isEqualTo("2");
                                                });
                                        assertThat(accepts.getLast())
                                                .satisfies(accept -> {
                                                    assertThat(accept.scheme()).isEqualTo(EXACT_SCHEME.name());
                                                    assertThat(accept.network()).isEqualTo("solana:EtWTRABZaYq6iMfeYKouRu166VU2xqa1");
                                                    assertThat(accept.amount()).isEqualTo("10000");
                                                    assertThat(accept.asset()).isEqualTo("4zMMC9srt5Ri5X14GAgXhaHii3GnPAEERYPJgZJDncDU");
                                                    assertThat(accept.payTo()).isEqualTo("CKPKJWNdJEqa81x7CkZ14BVPiY6y16Sxs7owznqtWYp5");
                                                    assertThat(accept.maxTimeoutSeconds()).isEqualTo(300);
                                                    assertThat(accept.getExtra("feePayer"))
                                                            .isPresent()
                                                            .get()
                                                            .isEqualTo("CKPKJWNdJEqa81x7CkZ14BVPiY6y16Sxs7owznqtWYp5");
                                                });
                                    });
                        });


            } else {
                fail("Expected HTTP 402 Payment Required from " + url + ", but got " + initialResponse.code());
            }

        } catch (IOException e) {
            fail("IOException during HTTP request to " + url + ": " + e.getMessage());
        }

    }

    private Map<String, String> getHeaders(Response response) {
        return response.headers().toMultimap()
                .entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
    }

}
