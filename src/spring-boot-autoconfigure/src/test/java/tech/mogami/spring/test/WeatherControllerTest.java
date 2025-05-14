package tech.mogami.spring.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.mogami.spring.autoconfigure.payload.PaymentHeader;
import tech.mogami.spring.autoconfigure.util.constants.X402Constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_SUPPORTED_VERSION;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_X_PAYMENT_HEADER;
import static tech.mogami.spring.autoconfigure.util.enums.Network.BASE_SEPOLIA;
import static tech.mogami.spring.test.constants.TestData.ASSET_CONTRACT_ADDRESS;
import static tech.mogami.spring.test.constants.TestData.SERVER_ADDRESS;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Weather controller tests")
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Weather free test")
    void freeWeather() throws Exception {
        mockMvc.perform(get("/weather/without-payment"))
                .andExpect(status().isOk())
                .andExpect(content().string("It's rainy!"));
    }

    @Test
    @DisplayName("Weather without payment header test")
    void weatherWithoutPaymentHeader() throws Exception {
        mockMvc.perform(get("/weather"))
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.x402Version").value(X402_SUPPORTED_VERSION))
                .andExpect(jsonPath("$.error").value(X402Constants.X402_PAYMENT_REQUIRED_MESSAGE))
                .andExpect(jsonPath("$.accepts[0].scheme").value("exact"))
                .andExpect(jsonPath("$.accepts[0].network").value(BASE_SEPOLIA.getValue()))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("1000"))
                .andExpect(jsonPath("$.accepts[0].resource").value("http://localhost/weather"))
                .andExpect(jsonPath("$.accepts[0].payTo").value(SERVER_ADDRESS))
                .andExpect(jsonPath("$.accepts[0].asset").value(ASSET_CONTRACT_ADDRESS));
    }

    @Test
    @DisplayName("Weather with payment header test")
    void weatherWithPaymentHeader() throws Exception {
        String paymentHeader = """
                {
                  "x402Version": 1,
                  "scheme": "exact",
                  "network": "base-sepolia",
                  "payload": {
                    "signature": "0x2d6a7588d6acca505cbf0d9a4a227e0c52c6c34008c8e8986a1283259764173608a2ce6496642e377d6da8dbbf5836e9bd15092f9ecab05ded3d6293af148b571c",
                    "authorization": {
                      "from": "0x857b06519E91e3A54538791bDbb0E22373e36b66",
                      "to": "0x209693Bc6afc0C5328bA36FaF03C514EF312287C",
                      "value": "10000",
                      "validAfter": "1740672089",
                      "validBefore": "1740672154",
                      "nonce": "0xf3746613c2d920b5fdabc0856f2aeb2d4f88ee6037b8cc5d04a71a4462f13480"
                    }
                  }
                }
                """.replaceAll("\\s+", "");

        var result = mockMvc.perform(get("/weather").header(X402_X_PAYMENT_HEADER, paymentHeader))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("It's sunny!")))
                .andReturn();

        // Testing the payment received.
        PaymentHeader payment = (PaymentHeader) result.getRequest().getAttribute(PaymentHeader.class.getName());
        assertThat(payment)
                .isNotNull()
                .satisfies(paymentHeader1 -> {
                    assertThat(paymentHeader1.x402Version()).isEqualTo(1);
                    assertThat(paymentHeader1.scheme()).isEqualTo("exact");
                    assertThat(paymentHeader1.network()).isEqualTo(BASE_SEPOLIA.getValue());
                    assertThat(paymentHeader1.payload().signature()).isEqualTo("0x2d6a7588d6acca505cbf0d9a4a227e0c52c6c34008c8e8986a1283259764173608a2ce6496642e377d6da8dbbf5836e9bd15092f9ecab05ded3d6293af148b571c");
                    assertThat(paymentHeader1.payload().authorization().from()).isEqualTo("0x857b06519E91e3A54538791bDbb0E22373e36b66");
                    assertThat(paymentHeader1.payload().authorization().to()).isEqualTo("0x209693Bc6afc0C5328bA36FaF03C514EF312287C");
                    assertThat(paymentHeader1.payload().authorization().value()).isEqualTo("10000");
                    assertThat(paymentHeader1.payload().authorization().validAfter()).isEqualTo(1740672089);
                    assertThat(paymentHeader1.payload().authorization().validBefore()).isEqualTo(1740672154);
                    assertThat(paymentHeader1.payload().authorization().nonce()).isEqualTo("0xf3746613c2d920b5fdabc0856f2aeb2d4f88ee6037b8cc5d04a71a4462f13480");
                });
    }

}
