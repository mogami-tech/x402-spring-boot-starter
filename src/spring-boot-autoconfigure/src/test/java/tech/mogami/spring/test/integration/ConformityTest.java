package tech.mogami.spring.test.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tech.mogami.commons.test.BaseTest;

import java.util.stream.Stream;

@AutoConfigureMockMvc
@DisplayName("Conformity tests")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "x402.facilitator.base-url=https://x402.org/facilitator"
)
// TODO Make this test work
@Disabled("Disabled to avoid too many requests to the public x402.org server")
public class ConformityTest extends BaseTest {

    @LocalServerPort
    int port;

    public Stream<String> urls(int port) {
        return Stream.of(
                "https://www.x402.org/protected",
                "http://localhost:" + port + "/protected"
        );
    }

    @Test
    @DisplayName("Payment process")
    void paymentProcess() throws JsonProcessingException {
//        // JSON value of output schema.
//        final JsonNode outputSchema = new ObjectMapper().readTree("""
//                {
//                  "input": {
//                    "discoverable": true,
//                    "method": "GET",
//                    "type": "http"
//                  }
//                }
//                """);
//
//        OkHttpClient client = new OkHttpClient();
//        urls(port).forEach(url -> {
//            Optional<PaymentRequired> paymentRequired;
//            PaymentRequirements paymentRequirements = null;
//
//            // Calling the URL without payment =========================================================================
//            try (Response response = client.newCall(new Request.Builder()
//                            .url(url)
//                            .addHeader("Accept", "application/json")
//                            .build())
//                    .execute()) {
//                // Testing response code (402 Payment Required).
//                assertThat(response).isNotNull();
//                assertThat(response.code()).isEqualTo(PAYMENT_REQUIRED.value());
//
//                // Testing payment required values.
//                assertThat(response.body()).isNotNull();
//                paymentRequired = X402PaymentHelper.getPaymentRequiredFromBody(response.body().string());
//                assertThat(paymentRequired).isPresent();
//                assertThat(paymentRequired.get().x402Version()).isEqualTo(X402_SUPPORTED_VERSION_BY_MOGAMI.version());
//                assertThat(paymentRequired.get().accepts()).size().isEqualTo(1);
//                assertThat(paymentRequired.get().error()).isEqualTo(X402_PAYMENT_REQUIRED_MESSAGE);
//
//                // Checking payment requirements values.
//                paymentRequirements = paymentRequired.get().accepts().getFirst();
//                assertThat(paymentRequirements).isNotNull()
//                        .satisfies(requirements -> {
//                            assertThat(requirements.scheme()).isEqualTo(EXACT_SCHEME_NAME);
//                            assertThat(requirements.network()).isEqualTo(BASE_SEPOLIA.name());
//                            assertThat(requirements.maxAmountRequiredAsBigInteger().compareTo(new BigInteger("10000"))).isEqualTo(0);
//                            assertThat(requirements.resource()).endsWith("/protected");
//                            assertThat(requirements.description()).isEqualTo("Access to protected content");
//                            assertThat(requirements.mimeType()).isEqualTo(APPLICATION_JSON.toString());
//                            assertThat(requirements.outputSchema()).isEqualTo(outputSchema);
//                            assertThat(requirements.payTo()).isEqualTo("0x209693Bc6afc0C5328bA36FaF03C514EF312287C");
//                            assertThat(requirements.maxTimeoutSeconds()).isEqualTo(300);
//                            assertThat(requirements.asset()).isEqualTo(BASE_SEPOLIA_USDC_CONTRACT);
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_NAME)).isPresent();
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_NAME)).get().isEqualTo("USDC");
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_VERSION)).isPresent();
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_VERSION)).get().isEqualTo("2");
//                        });
//
//            } catch (IOException e) {
//                fail("Request to " + url + " failed: " + e.getMessage());
//            }
//
//            // Calling the URL with invalid payment ====================================================================
//            // Generating a payment payload (without signature).
//            var paymentPayloadNotSigned = X402PaymentHelper.getPayloadFromPaymentRequirements(
//                    null,
//                    TEST_CLIENT_WALLET_ADDRESS_1,
//                    paymentRequirements);
//
//            try (Response response = client.newCall(new Request.Builder()
//                            .url(url)
//                            .header(X402_X_PAYMENT_HEADER, X402PaymentHelper.getPayloadHeader(paymentPayloadNotSigned))
//                            .addHeader("Accept", "application/json")
//                            .build())
//                    .execute()) {
//
//                // Testing response code (402 Payment Required).
//                assertThat(response).isNotNull();
//                assertThat(response.code()).isEqualTo(PAYMENT_REQUIRED.value());
//
//                // Testing payment required values.
//                assertThat(response.body()).isNotNull();
//                var body = response.body().string();
//                paymentRequired = X402PaymentHelper.getPaymentRequiredFromBody(body);
//                assertThat(paymentRequired).isPresent();
//                assertThat(paymentRequired.get().x402Version()).isEqualTo(X402_SUPPORTED_VERSION_BY_MOGAMI.version());
//                assertThat(paymentRequired.get().accepts()).size().isEqualTo(1);
//
//                // Error are different for invalid payment depending on the server implementation.
//                if (url.contains("x402.org")) {
//                    assertThat(paymentRequired.get().error()).contains("\"issues\":");
//                }
//                if (url.contains("localhost")) {
//                    assertThat(paymentRequired.get().error()).isEqualTo("invalid_payload");
//                }
//
//                // Checking payment requirements values.
//                paymentRequirements = paymentRequired.get().accepts().getFirst();
//                assertThat(paymentRequirements).isNotNull()
//                        .satisfies(requirements -> {
//                            assertThat(requirements.scheme()).isEqualTo(EXACT_SCHEME_NAME);
//                            assertThat(requirements.network()).isEqualTo(BASE_SEPOLIA.name());
//                            assertThat(requirements.maxAmountRequiredAsBigInteger().compareTo(new BigInteger("10000"))).isEqualTo(0);
//                            assertThat(requirements.resource()).endsWith("/protected");
//                            assertThat(requirements.description()).isEqualTo("Access to protected content");
//                            assertThat(requirements.mimeType()).isEqualTo(APPLICATION_JSON.toString());
//                            assertThat(requirements.outputSchema()).isEqualTo(outputSchema);
//                            assertThat(requirements.payTo()).isEqualTo("0x209693Bc6afc0C5328bA36FaF03C514EF312287C");
//                            assertThat(requirements.maxTimeoutSeconds()).isEqualTo(300);
//                            assertThat(requirements.asset()).isEqualTo(BASE_SEPOLIA_USDC_CONTRACT);
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_NAME)).isPresent();
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_NAME)).get().isEqualTo("USDC");
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_VERSION)).isPresent();
//                            assertThat(requirements.getExtra(EXACT_SCHEME_PARAMETER_VERSION)).get().isEqualTo("2");
//                        });
//
//            } catch (IOException e) {
//                fail("Request to " + url + " failed: " + e.getMessage());
//            }
//
//            // Calling the URL with invalid payment ====================================================================
//            // Generating a payment payload (without signature).
//            var signedPayload = X402PaymentHelper.getSignedPayload(
//                    Credentials.create(TEST_CLIENT_WALLET_ADDRESS_1_PRIVATE_KEY),
//                    paymentRequirements,
//                    paymentPayloadNotSigned);
//
//            try (Response response = client.newCall(new Request.Builder()
//                            .url(url)
//                            .header(X402_X_PAYMENT_HEADER, X402PaymentHelper.getPayloadHeader(signedPayload))
//                            .addHeader("Accept", "application/json")
//                            .build())
//                    .execute()) {
//
//                //var responseBody = response.body().string();
//                //System.out.println("=> Response after payment: " + responseBody);
//
//                // Testing response code - Should be 200 OK if payment is accepted.
//                assertThat(response).isNotNull();
//                assertThat(response.code()).isEqualTo(OK.value());
//
//                // Testing X402_X_PAYMENT_RESPONSE.
//                var paymentResponseHeaderEncoded = StringUtils.firstNonBlank(response.header(X402_X_PAYMENT_RESPONSE), response.header(X402_X_PAYMENT_RESPONSE.toLowerCase()));
//                assertThat(paymentResponseHeaderEncoded).isNotNull();
//                var paymentResponseHeaderDecoded = Base64Util.decode(paymentResponseHeaderEncoded);
//                assertThat(paymentResponseHeaderDecoded).isNotNull();
//                assertThat(JsonUtil.fromJson(paymentResponseHeaderDecoded, SettleResponse.class))
//                        .isNotNull()
//                        .satisfies(settleResponse -> {
//                            assertThat(settleResponse.success()).isTrue();
//                            assertThat(settleResponse.network()).isEqualTo(BASE_SEPOLIA.name());
//                            assertThat(settleResponse.transaction()).isNotNull();
//                            assertThat(settleResponse.errorReason()).isNull();
//                            assertThat(settleResponse.payer()).isEqualTo(TEST_CLIENT_WALLET_ADDRESS_1);
//                        });
//
//            } catch (IOException e) {
//                fail("Request to " + url + " failed: " + e.getMessage());
//            }
//        });
    }

}
