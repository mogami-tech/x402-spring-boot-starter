package tech.mogami.spring.test;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.mogami.spring.TestApplication;
import tech.mogami.spring.test.util.BaseTest;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Parameters tests")
public class ParametersTest extends BaseTest {

    @Test
    @DisplayName("Invalid default-receiver-address")
    void invalidDefaultReceiverAddress() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("default-receiver-address-invalid"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("Parameter mogami.default-receiver-address must be set"));
            assertTrue(message.contains("Invalid mogami.default-receiver-address value (must be something like 0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.facilitator-url"));
            assertFalse(message.contains("mogami.network"));
        }
    }

    @Test
    @DisplayName("Missing default-receiver-address")
    void missingDefaultReceiverAddress() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("default-receiver-address-missing"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'defaultReceiverAddress'"));
            assertTrue(message.contains("Parameter mogami.default-receiver-address must be set"));
            assertFalse(message.contains("Invalid mogami.default-receiver-address value (must be something like 0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.facilitator-url"));
            assertFalse(message.contains("mogami.network"));
        }
    }

    @Test
    @DisplayName("Null default-receiver-address")
    void nullDefaultReceiverAddress() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("default-receiver-address-null"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'defaultReceiverAddress'"));
            assertTrue(message.contains("Parameter mogami.default-receiver-address must be set"));
            assertFalse(message.contains("Invalid mogami.default-receiver-address value (must be something like 0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.facilitator-url"));
            assertFalse(message.contains("mogami.network"));
        }
    }

    @Test
    @DisplayName("Invalid facilitator-url")
    void invalidFacilitatorUrl() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-url-invalid"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'facilitatorUrl'"));
            assertFalse(message.contains("Parameter mogami.facilitator-url must be set"));
            assertTrue(message.contains("Invalid mogami.facilitator-url value (must be something like https://x402.org/facilitator)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.default-receiver-address"));
            assertFalse(message.contains("mogami.network"));
        }
    }

    @Test
    @DisplayName("Missing facilitator-url")
    void missingFacilitatorUrl() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-url-missing"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'facilitatorUrl'"));
            assertTrue(message.contains("Parameter mogami.facilitator-url must be set"));
            assertFalse(message.contains("Invalid mogami.facilitator-url value (must be something like https://x402.org/facilitator)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.default-receiver-address"));
            assertFalse(message.contains("mogami.network"));
        }
    }

    @Test
    @DisplayName("Null facilitator-url")
    void nullFacilitatorUrl() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-url-null"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'facilitatorUrl'"));
            assertTrue(message.contains("null"));
            assertFalse(message.contains("Invalid mogami.facilitator-url value (must be something like https://x402.org/facilitator))"));
            // Fields without errors.
            assertFalse(message.contains("mogami.default-receiver-address"));
            assertFalse(message.contains("mogami.network"));
        }
    }

    @Test
    @DisplayName("Invalid network")
    void invalidNetwork() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-network-invalid"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'network'"));
            assertFalse(message.contains("Parameter mogami.network must be set"));
            assertTrue(message.contains("Invalid mogami.network value (must be something like base-sepolia)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.default-receiver-address"));
            assertFalse(message.contains("mogami.facilitator-url"));
        }
    }

    @Test
    @DisplayName("Missing network")
    void missingNetwork() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-network-missing"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'network'"));
            assertTrue(message.contains("Parameter mogami.network must be set"));
            assertFalse(message.contains("Invalid mogami.network value (must be something like base-sepolia)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.default-receiver-address"));
            assertFalse(message.contains("mogami.facilitator-url"));
        }
    }

    @Test
    @DisplayName("Null network")
    void nullNetwork() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("facilitator-network-null"));
            application.run();
            fail("Exception not raised with invalid parameters");
        } catch (Exception e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'network'"));
            assertTrue(message.contains("Parameter mogami.network must be set"));
            assertFalse(message.contains("Invalid mogami.network value (must be something like base-sepolia)"));
            // Fields without errors.
            assertFalse(message.contains("mogami.default-receiver-address"));
            assertFalse(message.contains("mogami.facilitator-url"));
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
            // Fields with errors.
            assertTrue(message.contains("'defaultReceiverAddress'"));
            assertTrue(message.contains("'facilitatorUrl'"));
            assertTrue(message.contains("'network'"));
        }
    }

    @Test
    @DisplayName("Valid parameters")
    void validParameters() {
        try {
            var application = new SpringApplication(TestApplication.class);
            application.setDefaultProperties(getTestProperties("valid-parameters"));
            application.run();
        } catch (Exception e) {
            fail("Exception not raised with valid parameters");
        }
    }

}
