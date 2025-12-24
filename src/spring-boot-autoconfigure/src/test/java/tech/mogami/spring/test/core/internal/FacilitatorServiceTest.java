package tech.mogami.spring.test.core.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.mogami.commons.api.facilitator.supported.SupportedResponse;
import tech.mogami.spring.provider.facilitator.FacilitatorService;
import tech.mogami.spring.test.util.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(properties = "x402.facilitator.base-url=https://x402.org/facilitator")
@AutoConfigureMockMvc
@DisplayName("Facilitator client tests")
public class FacilitatorServiceTest extends BaseTest {

    @Autowired
    private FacilitatorService facilitatorService;

    @Test
    @DisplayName("/supported response")
    void supportedResponse() {
        assertThat(facilitatorService.supported().block())
                .isNotNull()
                .satisfies(supportedResponse -> {
                    // Supported kinds.
                    assertThat(supportedResponse.kinds())
                            .extracting(
                                    SupportedResponse.SupportedKind::x402Version,
                                    SupportedResponse.SupportedKind::scheme,
                                    SupportedResponse.SupportedKind::network
                            )
                            .containsExactly(
                                    tuple(2, "exact", "eip155:84532"),
                                    tuple(2, "exact", "solana:EtWTRABZaYq6iMfeYKouRu166VU2xqa1"),
                                    tuple(1, "exact", "base-sepolia"),
                                    tuple(1, "exact", "solana-devnet")
                            );
                    // Supported extensions.
                    assertThat(supportedResponse.kinds())
                            .filteredOn(kind -> kind.network().startsWith("solana"))
                            .allSatisfy(kind -> assertThat(kind.extra()).isNotNull().containsKey("feePayer"));
                    // Supported signers.
                    assertThat(supportedResponse.signers().get("eip155:*"))
                            .containsExactly("0xd407e409E34E0b9afb99EcCeb609bDbcD5e7f1bf");
                    assertThat(supportedResponse.signers().get("solana:*"))
                            .containsExactly("CKPKJWNdJEqa81x7CkZ14BVPiY6y16Sxs7owznqtWYp5");
                });
    }

    @Test
    @DisplayName("/verify response")
    void verifyResponse() {
        fail("TODO Fix this test");
//        assertThat(facilitatorService.verify(getSamplePaymentPayload(), getSamplePaymentRequirements()).block())
//                .isNotNull()
//                .satisfies(verifyResponse -> {
//                    assertThat(verifyResponse.isValid()).isFalse();
//                    assertThat(verifyResponse.invalidReason()).isEqualTo("invalid_exact_evm_payload_authorization_valid_before");
//                    assertThat(verifyResponse.payer()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
//                });
    }

    @Test
    @DisplayName("/settle response")
    void settleResponse() {
        fail("TODO Fix this test");
//        assertThat(facilitatorService.settle(getSamplePaymentPayload(), getSamplePaymentRequirements()).block())
//                .isNotNull()
//                .satisfies(settleResult -> {
//                    assertThat(settleResult.success()).isFalse();
//                    assertThat(settleResult.network()).isEqualTo(BASE_SEPOLIA.name());
//                    assertThat(settleResult.transaction()).isNull();
//                    assertThat(settleResult.errorReason()).isEqualTo("invalid_exact_evm_payload_authorization_valid_before");
//                    assertThat(settleResult.payer()).isEqualTo("0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73");
//                });
    }

}
