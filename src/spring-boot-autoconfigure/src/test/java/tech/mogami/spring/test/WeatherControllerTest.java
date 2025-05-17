package tech.mogami.spring.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.mogami.spring.autoconfigure.dto.ExactSchemePayment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_PAYMENT_REQUIRED_MESSAGE;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_SUPPORTED_VERSION;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_X_PAYMENT_HEADER;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_X_PAYMENT_HEADER_DECODED;
import static tech.mogami.spring.autoconfigure.util.constants.networks.BaseNetworks.BASE_SEPOLIA;
import static tech.mogami.spring.autoconfigure.util.constants.schemes.ExactSchemeConstants.EXACT_SCHEME_NAME;
import static tech.mogami.spring.test.constants.TestData.ASSET_CONTRACT_ADDRESS;
import static tech.mogami.spring.test.constants.TestData.SERVER_WALLET_ADDRESS_1;
import static tech.mogami.spring.test.constants.TestData.SERVER_WALLET_ADDRESS_2;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Weather controller tests")
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("get /weather/without-payment test")
    void getFreeWeather() throws Exception {
        mockMvc.perform(get("/weather/without-payment"))
                .andExpect(status().isOk())
                .andExpect(content().string("It's rainy!"));
    }

    @Test
    @DisplayName("get /weather without payment header test")
    void getWeatherWithoutPaymentHeader() throws Exception {
        mockMvc.perform(get("/weather"))
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
                .andExpect(jsonPath("$.accepts[0].payTo").value(SERVER_WALLET_ADDRESS_1))
                .andExpect(jsonPath("$.accepts[0].asset").value(ASSET_CONTRACT_ADDRESS))
                // Second scheme.
                .andExpect(jsonPath("$.accepts[1].scheme").value(EXACT_SCHEME_NAME))
                .andExpect(jsonPath("$.accepts[1].network").value(BASE_SEPOLIA))
                .andExpect(jsonPath("$.accepts[1].maxAmountRequired").value("2000"))
                .andExpect(jsonPath("$.accepts[1].description").value("Description number 2"))
                .andExpect(jsonPath("$.accepts[1].resource").value("http://localhost/weather"))
                .andExpect(jsonPath("$.accepts[1].payTo").value(SERVER_WALLET_ADDRESS_2))
                .andExpect(jsonPath("$.accepts[1].asset").value(ASSET_CONTRACT_ADDRESS));
    }

    @Test
    @DisplayName("get /weather with invalid payment header test")
    void getWeatherWithInvalidPaymentHeader() throws Exception {

        // We create the header and encode it to Base64.
        String paymentHeader = """
                {
                    "x402Version": 1,
                    "scheme": "exact",
                    "network": "base-sepolia",
                    "payload": {
                      "signature": "0x1c7e56451968cc2c2816fc776c6f75483815408b2e087d568ce7e8509c59911b3c9353dbdff8b565680e9defd52336eb2213dfd83f1a07c20625e53d8fda2b951b",
                      "authorization": {
                        "from": "0x857b06519E91e3A54538791bDbb0E22373e36b66",
                        "to": "0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73",
                        "value": "1000",
                        "validAfter": "1747486410",
                        "validBefore": "1747486530",
                        "nonce": "0xa4f160b60eae4968ba966abf1836fb51a38da88f7749fbc1df8cf2701c7561c2"
                      }
                    }
                  }""".replaceAll("\\s+", "");
        String encodedPaymentHeader = Base64
                .getEncoder()
                .encodeToString(paymentHeader.getBytes(StandardCharsets.UTF_8));

        // Calling the API with the payment header.
        var result = mockMvc.perform(get("/weather").header(X402_X_PAYMENT_HEADER, encodedPaymentHeader))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("It's sunny!")))
                .andReturn();

        // Testing the header payment received.
        assertThat((ExactSchemePayment) result.getRequest().getAttribute(X402_X_PAYMENT_HEADER_DECODED))
                .isNotNull()
                .satisfies(paymentHeader1 -> {
                    assertThat(paymentHeader1.x402Version()).isEqualTo(1);
                    assertThat(paymentHeader1.scheme()).isEqualTo(EXACT_SCHEME_NAME);
                    assertThat(paymentHeader1.network()).isEqualTo(BASE_SEPOLIA);
                    assertThat(paymentHeader1.payload().signature()).isEqualTo("0x1c7e56451968cc2c2816fc776c6f75483815408b2e087d568ce7e8509c59911b3c9353dbdff8b565680e9defd52336eb2213dfd83f1a07c20625e53d8fda2b951b");
                    assertThat(paymentHeader1.payload().authorization().from()).isEqualTo("0x857b06519E91e3A54538791bDbb0E22373e36b66");
                    assertThat(paymentHeader1.payload().authorization().to()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
                    assertThat(paymentHeader1.payload().authorization().value()).isEqualTo("1000");
                    assertThat(paymentHeader1.payload().authorization().validAfter()).isEqualTo(1747486410);
                    assertThat(paymentHeader1.payload().authorization().validBefore()).isEqualTo(1747486530);
                    assertThat(paymentHeader1.payload().authorization().nonce()).isEqualTo("0xa4f160b60eae4968ba966abf1836fb51a38da88f7749fbc1df8cf2701c7561c2");
                });
    }

}
