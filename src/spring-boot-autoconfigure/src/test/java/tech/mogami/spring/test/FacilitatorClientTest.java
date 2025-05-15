package tech.mogami.spring.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.mogami.spring.autoconfigure.provider.facilitator.FacilitatorClient;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.mogami.spring.autoconfigure.util.enums.Network.BASE_SEPOLIA;

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
                    assertThat(supportedResponse.kinds().getFirst().network()).isEqualTo(BASE_SEPOLIA.getValue());
                });
    }

}
