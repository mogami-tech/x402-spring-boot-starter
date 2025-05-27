package tech.mogami.spring.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorService;
import tech.mogami.spring.test.util.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.mogami.commons.constants.networks.BaseNetworks.BASE_SEPOLIA;
import static tech.mogami.commons.header.payment.schemes.ExactSchemeConstants.EXACT_SCHEME_NAME;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Facilitator client tests")
public class FacilitatorServiceTest extends BaseTest {

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
        assertThat(facilitatorService.verify(getSamplePaymentPayload(), getSamplePaymentRequirements()).block())
                .isNotNull()
                .satisfies(verifyResponse -> {
                    assertThat(verifyResponse.isValid()).isFalse();
                    assertThat(verifyResponse.invalidReason()).isEqualTo("invalid_scheme");
                    assertThat(verifyResponse.payer()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
                });
    }

    @Test
    @DisplayName("Settle response test")
    void settleResponse() {
        assertThat(facilitatorService.settle(getSamplePaymentPayload(), getSamplePaymentRequirements()).block())
                .isNotNull()
                .satisfies(settleResult -> {
                    assertThat(settleResult.success()).isFalse();
                    assertThat(settleResult.network()).isEqualTo(BASE_SEPOLIA);
                    assertThat(settleResult.transaction()).isEmpty();
                    assertThat(settleResult.errorReason()).isEqualTo("invalid_scheme");
                    assertThat(settleResult.payer()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
                });
    }

}
