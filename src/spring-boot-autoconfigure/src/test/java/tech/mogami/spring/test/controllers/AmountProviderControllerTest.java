package tech.mogami.spring.test.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Amount provider tests")
public class AmountProviderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("X402PaymentRequirements with basic amount provider")
    void dynamicAmountWithX402PaymentRequirements() throws Exception {
        // No type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PaymentRequirements"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("3001"));
        // With type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PaymentRequirements").param("type", "image"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("1001"));
    }

    @Test
    @DisplayName("X402PaymentRequirements with spring amount provider")
    void dynamicAmountWithX402PaymentRequirementsWithSpring() throws Exception {
        // No type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PaymentRequirementsWithSpring"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("6002"));

        // With type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PaymentRequirementsWithSpring").param("type", "text"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("5002"));
    }

    @Test
    @DisplayName("X402PayUSDC with basic amount provider")
    void dynamicAmountWithX402PayUSDC() throws Exception {
        // No type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PayUSDC"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("3001"));

        // With type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PayUSDC").param("type", "text"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("2001"));
    }

    @Test
    @DisplayName("X402PayUSDC with spring amount provider")
    void dynamicAmountWithX402PayUSDCWithSpring() throws Exception {
        // No type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PayUSDCWithSpring"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("6002"));

        // With type parameter.
        mockMvc.perform(get("/dynamicAmountWithX402PayUSDCWithSpring").param("type", "image"))
                .andDo(print())
                .andExpect(status().isPaymentRequired())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accepts[0].maxAmountRequired").value("4002"));
    }


}
