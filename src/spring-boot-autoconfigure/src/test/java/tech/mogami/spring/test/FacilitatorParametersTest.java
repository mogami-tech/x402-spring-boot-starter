package tech.mogami.spring.test;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.mogami.commons.test.BaseTest;
import tech.mogami.spring.TestApplication;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Facilitator parameters tests")
public class FacilitatorParametersTest extends BaseTest {

    @Test
    @DisplayName("Missing facilitator parameters")
    void missingFacilitatorParameters() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-missing"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("Parameters for x402.facilitator must be set"));
        }
    }

    @Test
    @DisplayName("Missing facilitator.base-url parameter")
    void missingFacilitatorBaseUrlParameter() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-base-url-missing"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("Parameters for x402.facilitator must be set"));
        }
    }

    @Test
    @DisplayName("Empty facilitator.base-url parameter")
    void emptyFacilitatorBaseUrlParameter() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-base-url-null"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("Parameter x402.facilitator.base-url must be set"));
        }
    }

    @Test
    @DisplayName("Invalid facilitator.base-url parameter")
    void invalidFacilitatorBaseUrlParameter() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-base-url-invalid"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("Invalid x402.facilitator.base-url value (must be something like https://x402.org/facilitator)"));
        }
    }

    @Test
    @DisplayName("No parameters")
    void noParameters() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("no-parameters"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("Parameters for x402.facilitator must be set"));
        }
    }

}
