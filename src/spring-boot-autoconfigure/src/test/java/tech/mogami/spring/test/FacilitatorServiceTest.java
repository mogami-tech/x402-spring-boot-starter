package tech.mogami.spring.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.mogami.spring.autoconfigure.dto.PaymentPayload;
import tech.mogami.spring.autoconfigure.dto.PaymentRequirements;
import tech.mogami.spring.autoconfigure.dto.schemes.ExactSchemePayload;
import tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.mogami.spring.autoconfigure.dto.schemes.ExactSchemeConstants.EXACT_SCHEME_NAME;
import static tech.mogami.spring.autoconfigure.util.constants.networks.BaseNetworks.BASE_SEPOLIA;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Facilitator client tests")
public class FacilitatorServiceTest {

    @Autowired
    private FacilitatorService facilitatorService;

    @Test
    @DisplayName("Supported response test")
    void supportedResponse() {
        assertThat(facilitatorService.supported().block())
                .isNotNull()
                .satisfies(supportedResponse -> {
                    assertThat(supportedResponse.kinds()).isNotEmpty();
                    assertThat(supportedResponse.kinds().getFirst().scheme()).isEqualTo(EXACT_SCHEME_NAME);
                    assertThat(supportedResponse.kinds().getFirst().network()).isEqualTo(BASE_SEPOLIA);
                });
    }

    @Test
    @DisplayName("Verify response test")
    void verifyResponse() {
        var paymentPayload = PaymentPayload.builder()
                .x402Version(1)
                .scheme(EXACT_SCHEME_NAME)
                .network(BASE_SEPOLIA)
                .payload(ExactSchemePayload.builder()
                        .signature("0xf268bbac717601c718075e60461516d6d36302e3d3c07be5c58a89d3dc10b3bf5dfc813c446f82c0f71e9dfef47fd894e16fc64d666553c89717f7730b3698531c")
                        .authorization(ExactSchemePayload.Authorization.builder()
                                .from("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73")
                                .to("0x7553F6FA4Fb62986b64f79aEFa1fB93ea64A22b1")
                                .value("1000")
                                .validAfter("1747639463")
                                .validBefore("1747639583")
                                .nonce("0x1d7e10376afe381748b69ffd53b87de4213374957a1adeea46cf3ccf9aebf199")
                                .build())
                        .build())
                .build();

        var paymentRequirements = PaymentRequirements.builder()
                .scheme(EXACT_SCHEME_NAME)
                .network(BASE_SEPOLIA)
                .maxAmountRequired("1000")
                .resource("http://localhost:4021/weather")
                .description("")
                .mimeType("")
                .payTo("0x7553F6FA4Fb62986b64f79aEFa1fB93ea64A22b1")
                .maxTimeoutSeconds(60)
                .asset("0x036CbD53842c5426634e7929541eC2318f3dCF7e")
                .extra(Map.of("name", "USDC"))
                .extra(Map.of("version", "2"))
                .build();

        assertThat(facilitatorService.verify(paymentPayload, paymentRequirements).block())
                .isNotNull()
                .satisfies(verifyResponse -> {
                    assertThat(verifyResponse.valid()).isFalse();
                    assertThat(verifyResponse.invalidReason()).isEqualTo("invalid_scheme");
                    assertThat(verifyResponse.payer()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
                });
    }

}
