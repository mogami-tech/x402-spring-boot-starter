package tech.mogami.spring.integration;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tech.mogami.commons.test.BaseTest;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.HttpStatus.PAYMENT_REQUIRED;

@AutoConfigureMockMvc
@DisplayName("Conformity tests")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "x402.facilitator.base-url=https://x402.org/facilitator"
)
public class ConformityTest extends BaseTest {

    @LocalServerPort
    int port;

    public Stream<String> urls(int port) {
        return Stream.of(
                "https://www.x402.org/protected",
                "http://localhost:" + port + "/protected"
        );
    }

    @Test
    @DisplayName("Calling the URL without payment requirements")
    void withoutPaymentRequirements() throws Exception {
        OkHttpClient client = new OkHttpClient();
        urls(port).forEach(url -> {
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "axios/1.8.4")
                    .addHeader("Accept", "application/json")
                    .build();

            // Testing the response.
            try (Response response = client.newCall(request).execute()) {
                assertThat(response).isNotNull();
                assertThat(response.code()).isEqualTo(PAYMENT_REQUIRED.value());
                assertThat(response.body()).isNotNull();

                // Looking at the payment requirements.
                var body = response.body().string();
                System.out.println("===> " + body);
                //assertThat(X402PaymentHelper.getPaymentRequiredFromBody(body)).isPresent();

            } catch (IOException e) {
                fail("Request to " + url + " failed: " + e.getMessage());
            }
        });
    }

}
