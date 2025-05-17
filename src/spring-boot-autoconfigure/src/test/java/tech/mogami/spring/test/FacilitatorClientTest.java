package tech.mogami.spring.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.mogami.spring.autoconfigure.payload.PaymentHeader;
import tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorClient;
import tech.mogami.spring.autoconfigure.provider.facilitator.verify.PaymentRequirements;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.mogami.spring.autoconfigure.util.constants.networks.BaseNetworks.BASE_SEPOLIA;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Facilitator client tests")
public class FacilitatorClientTest {

    @Autowired
    private FacilitatorClient facilitatorClient;

    @Test
    @DisplayName("Supported response test")
    void supportedResponse() {
        assertThat(facilitatorClient.supported().block())
                .isNotNull()
                .satisfies(supportedResponse -> {
                    assertThat(supportedResponse.kinds()).isNotEmpty();
                    assertThat(supportedResponse.kinds().getFirst().scheme()).isEqualTo("exact");
                    assertThat(supportedResponse.kinds().getFirst().network()).isEqualTo(BASE_SEPOLIA);
                });
    }

    @Test
    @Disabled
    @DisplayName("Verify response test")
    void verifyResponse() throws Exception {
        PaymentHeader paymentHeader = PaymentHeader.builder()
                .x402Version(1)
                .scheme("exact")
                .network("base-sepolia")
                .payload(PaymentHeader.Payload.builder()
                        .signature("0x00a16572b50047a9e772127fe684e2a8bfed31ab1857962fd702b021c45a8fe6249a211769aeec05e4667093cb186f15b13c683dc1c475c509190decb9ac4de01c")
                        .authorization(PaymentHeader.Payload.Authorization.builder()
                                .from("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73")
                                .to("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73")
                                .value(BigInteger.valueOf(1000))
                                .validAfter(1747331595)
                                .validBefore(1747331715)
                                .nonce("0xc823e5d814b5f0e3d38168a43ca0b995d3a15a2fcd7b946ddd00ca5a6d41d1f3")
                                .build())
                        .build())
                .build();

        PaymentRequirements paymentRequirements = PaymentRequirements.builder()
                .scheme("exact")
                .network("base-sepolia")
                .payTo("0x209693Bc6afc0C5328bA36FaF03C514EF312287C")
                .asset("0x036CbD53842c5426634e7929541eC2318f3dCF7e")
                .maxAmountRequired(BigInteger.valueOf(1000))
                .build();

        assertThat(facilitatorClient.verify(paymentHeader, paymentRequirements).block())
                .isNotNull()
                .satisfies(resp -> {
                    assertThat(resp.valid()).isTrue();
                    assertThat(resp.reason()).isEqualTo("OK");
                });
    }

}
