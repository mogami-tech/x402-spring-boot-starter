package tech.mogami.spring.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.mogami.commons.api.facilitator.settle.SettleResponse;
import tech.mogami.commons.header.payment.PaymentPayload;
import tech.mogami.commons.header.payment.schemes.ExactSchemePayload;
import tech.mogami.commons.test.BaseTest;

import java.io.IOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.MediaType.APPLICATION_JSON;
import static org.mockserver.model.StringBody.subString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static tech.mogami.commons.constant.X402Constants.X402_PAYMENT_REQUIRED_MESSAGE;
import static tech.mogami.commons.constant.X402Constants.X402_SUPPORTED_VERSION;
import static tech.mogami.commons.constant.X402Constants.X402_X_PAYMENT_HEADER;
import static tech.mogami.commons.constant.X402Constants.X402_X_PAYMENT_HEADER_DECODED;
import static tech.mogami.commons.constant.X402Constants.X402_X_PAYMENT_RESPONSE;
import static tech.mogami.commons.constant.networks.BaseNetworks.BASE_SEPOLIA;
import static tech.mogami.commons.header.payment.schemes.ExactSchemeConstants.EXACT_SCHEME_NAME;

@SpringBootTest(
        properties = {
                "x402.facilitator.base-url=http://localhost:10000/facilitator",
        })
@AutoConfigureMockMvc
@DisplayName("Weather controller tests")
public class WeatherControllerTest extends BaseTest {

    private static ClientAndServer mockServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockServer = ClientAndServer.startClientAndServer(10000);
        // get /weather with invalid payment header test
        mockServer.when(request().withPath("/facilitator/verify").withBody(subString("isValidFalse"))
        ).respond(response()
                .withStatusCode(200).withContentType(APPLICATION_JSON)
                .withBody("""
                        {
                          "isValid": false,
                          "invalidReason": "invalid_scheme",
                          "payer": "0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F71"
                        }
                        """)
        );
        mockServer.when(request().withPath("/facilitator/verify").withBody(subString("isValidTrue"))
        ).respond(response()
                .withStatusCode(200).withContentType(APPLICATION_JSON)
                .withBody("""
                        {
                          "isValid": true,
                          "payer": "0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F72"
                        }
                        """)
        );
        // get /weather with valid payment header test
        mockServer.when(request().withPath("/facilitator/settle").withBody(subString("isValidTrue"))
        ).respond(response()
                .withStatusCode(200).withContentType(APPLICATION_JSON)
                .withBody("""
                        {
                          "success": true,
                          "network": "base-sepolia",
                          "transaction": "0x7cbf21c639f7bcd8e68ba02b83b34187f686577a0cead0d7c6f0f57183a84b51",
                          "errorReason": "invalid_scheme",
                          "payer": "0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73"
                        }
                        """)
        );
    }

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    @DisplayName("get /weather/without-payment test")
    void getFreeWeather() throws Exception {
        mockMvc.perform(get("/weather/without-payment"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("It's rainy!"));
    }

    @Test
    @DisplayName("get /weather without payment header test")
    void getWeatherWithoutPaymentHeader() throws Exception {
        mockMvc.perform(get("/weather"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.x402Version").value(X402_SUPPORTED_VERSION))
                .andExpect(jsonPath("$.error").value(X402_PAYMENT_REQUIRED_MESSAGE))
                .andExpect(jsonPath("$.accepts.length()").value(2))
                // First scheme.
                .andExpect(jsonPath("$.accepts[0].scheme").value(EXACT_SCHEME_NAME))
                .andExpect(jsonPath("$.accepts[0].network").value(BASE_SEPOLIA))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("1000"))
                .andExpect(jsonPath("$.accepts[0].description").isEmpty())
                .andExpect(jsonPath("$.accepts[0].resource").value("http://localhost/weather"))
                .andExpect(jsonPath("$.accepts[0].payTo").value(TEST_SERVER_WALLET_ADDRESS_1))
                .andExpect(jsonPath("$.accepts[0].asset").value(TEST_ASSET_CONTRACT_ADDRESS))
                .andExpect(jsonPath("$.accepts[0].extra.name").value("USDC"))
                .andExpect(jsonPath("$.accepts[0].extra.version").value("2"))
                // Second scheme.
                .andExpect(jsonPath("$.accepts[1].scheme").value(EXACT_SCHEME_NAME))
                .andExpect(jsonPath("$.accepts[1].network").value(BASE_SEPOLIA))
                .andExpect(jsonPath("$.accepts[1].maxAmountRequired").value("2000"))
                .andExpect(jsonPath("$.accepts[1].description").value("Description number 2"))
                .andExpect(jsonPath("$.accepts[1].resource").value("http://localhost/weather"))
                .andExpect(jsonPath("$.accepts[1].payTo").value(TEST_SERVER_WALLET_ADDRESS_2))
                .andExpect(jsonPath("$.accepts[1].asset").value(TEST_ASSET_CONTRACT_ADDRESS))
                .andExpect(jsonPath("$.accepts[1].extra").isEmpty());
    }

    @Test
    @DisplayName("get /weather with invalid payment header test")
    void getWeatherWithInvalidPaymentHeader() throws Exception {
        // Calling the API with the payment header.
        var result = mockMvc.perform(get("/weather").header(X402_X_PAYMENT_HEADER, getSampleEncodedPaymentHeader("isValidFalse")))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.x402Version").value(X402_SUPPORTED_VERSION))
                .andExpect(jsonPath("$.error").value(X402_PAYMENT_REQUIRED_MESSAGE))
                .andExpect(jsonPath("$.accepts.length()").value(2))
                .andReturn();

        // Testing the decoded payment payload received in the response.
        assertThat((PaymentPayload) result.getRequest().getAttribute(X402_X_PAYMENT_HEADER_DECODED))
                .isNotNull()
                .satisfies(paymentPayload -> {
                    assertThat(paymentPayload.x402Version()).isEqualTo(1);
                    assertThat(paymentPayload.scheme()).isEqualTo(EXACT_SCHEME_NAME);
                    assertThat(paymentPayload.network()).isEqualTo(BASE_SEPOLIA);
                    assertThat((ExactSchemePayload) paymentPayload.payload())
                            .isNotNull()
                            .satisfies(payload -> {
                                assertThat(payload).isInstanceOf(ExactSchemePayload.class);
                                assertThat(payload.signature()).isEqualTo("0x1c7e56451968cc2c2816fc776c6f75483815408b2e087d568ce7e8509c59911b3c9353dbdff8b565680e9defd52336eb2213dfd83f1a07c20625e53d8fda2b951b");
                                assertThat(payload.authorization().from()).isEqualTo("0x857b06519E91e3A54538791bDbb0E22373e36b66");
                                assertThat(payload.authorization().to()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
                                assertThat(payload.authorization().value()).isEqualTo("1000");
                                assertThat(payload.authorization().validAfter()).isEqualTo("1747486410");
                                assertThat(payload.authorization().validBefore()).isEqualTo("1747486530");
                                assertThat(payload.authorization().nonce()).isEqualTo("isValidFalse");
                            });
                });
    }

    @Test
    @DisplayName("get /weather with valid payment header test")
    void getWeatherWithValidPaymentHeader() throws Exception {
        // Calling the API with the payment header.
        var result = mockMvc.perform(get("/weather").header(X402_X_PAYMENT_HEADER, getSampleEncodedPaymentHeader("isValidTrue")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("It's sunny!"))
                .andReturn();

        // Testing the decoded payment payload received in the response.
        assertThat((PaymentPayload) result.getRequest().getAttribute(X402_X_PAYMENT_HEADER_DECODED))
                .isNotNull()
                .satisfies(paymentPayload -> {
                    assertThat(paymentPayload.x402Version()).isEqualTo(1);
                    assertThat(paymentPayload.scheme()).isEqualTo(EXACT_SCHEME_NAME);
                    assertThat(paymentPayload.network()).isEqualTo(BASE_SEPOLIA);
                    assertThat((ExactSchemePayload) paymentPayload.payload())
                            .isNotNull()
                            .satisfies(payload -> {
                                assertThat(payload).isInstanceOf(ExactSchemePayload.class);
                                assertThat(payload.signature()).isEqualTo("0x1c7e56451968cc2c2816fc776c6f75483815408b2e087d568ce7e8509c59911b3c9353dbdff8b565680e9defd52336eb2213dfd83f1a07c20625e53d8fda2b951b");
                                assertThat(payload.authorization().from()).isEqualTo("0x857b06519E91e3A54538791bDbb0E22373e36b66");
                                assertThat(payload.authorization().to()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
                                assertThat(payload.authorization().value()).isEqualTo("1000");
                                assertThat(payload.authorization().validAfter()).isEqualTo("1747486410");
                                assertThat(payload.authorization().validBefore()).isEqualTo("1747486530");
                                assertThat(payload.authorization().nonce()).isEqualTo("isValidTrue");
                            });
                });

        // Testing that the response contains the X-PAYMENT-RESPONSE header.
        try {
            final String decodeSettleString = new String(Base64.getMimeDecoder().decode(result.getResponse().getHeader(X402_X_PAYMENT_RESPONSE)), UTF_8);
            final SettleResponse settleResponse = new ObjectMapper().readValue(decodeSettleString, SettleResponse.class);
            assertThat(settleResponse)
                    .isNotNull()
                    .satisfies(resultSettle -> {
                        assertThat(resultSettle.success()).isTrue();
                        assertThat(resultSettle.network()).isEqualTo(BASE_SEPOLIA);
                        assertThat(resultSettle.transaction()).isEqualTo("0x7cbf21c639f7bcd8e68ba02b83b34187f686577a0cead0d7c6f0f57183a84b51");
                        assertThat(resultSettle.errorReason()).isEqualTo("invalid_scheme");
                        assertThat(resultSettle.payer()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
                    });
        } catch (IOException e) {
            fail("Invalid X-PAYMENT-RESPONSE header", e);
        }
    }

}
