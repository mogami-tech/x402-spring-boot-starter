package tech.mogami.spring.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.mogami.spring.autoconfigure.util.constants.X402Constants;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static tech.mogami.spring.autoconfigure.util.constants.X402Constants.X402_SUPPORTED_VERSION;
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
    void weatherWithoutPayment() throws Exception {
        mockMvc.perform(get("/weather/without-payment"))
                .andExpect(status().isOk())
                .andExpect(content().string("It's rainy!"));
    }

    @Test
    void weatherRequiresPayment() throws Exception {
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

}
