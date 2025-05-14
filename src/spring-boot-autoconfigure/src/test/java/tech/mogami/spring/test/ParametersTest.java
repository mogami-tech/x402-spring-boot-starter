package tech.mogami.spring.test;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindException;
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("null"));
            assertTrue(message.contains("Invalid address (must be something like 0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73)"));
            // Fields without errors.
            assertFalse(message.contains("'facilitatorUrl'"));
            assertFalse(message.contains("'network'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'defaultReceiverAddress'"));
            assertTrue(message.contains("null"));
            assertFalse(message.contains("Invalid address (must be something like 0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73)"));
            // Fields without errors.
            assertFalse(message.contains("'facilitatorUrl'"));
            assertFalse(message.contains("'network'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'defaultReceiverAddress'"));
            assertTrue(message.contains("null"));
            assertFalse(message.contains("Invalid address (must be something like 0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73)"));
            // Fields without errors.
            assertFalse(message.contains("'facilitatorUrl'"));
            assertFalse(message.contains("'network'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'facilitatorUrl'"));
            assertFalse(message.contains("null"));
            assertTrue(message.contains("Invalid URL (must be something like https://x402.org/facilitator)"));
            // Fields without errors.
            assertFalse(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("'network'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'facilitatorUrl'"));
            assertTrue(message.contains("null"));
            assertFalse(message.contains("Invalid URL (must be something like https://x402.org/facilitator)"));
            // Fields without errors.
            assertFalse(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("'network'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'facilitatorUrl'"));
            assertTrue(message.contains("null"));
            assertFalse(message.contains("Invalid URL (must be something like https://x402.org/facilitator)"));
            // Fields without errors.
            assertFalse(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("'network'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'network'"));
            assertFalse(message.contains("null"));
            assertTrue(message.contains("Invalid network (must be something like base-sepolia)"));
            // Fields without errors.
            assertFalse(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("'facilitatorUrl'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'network'"));
            assertTrue(message.contains("null"));
            assertFalse(message.contains("Invalid network (must be something like base-sepolia)"));
            // Fields without errors.
            assertFalse(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("'facilitatorUrl'"));
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
        } catch (ConfigurationPropertiesBindException e) {
            var message = ExceptionUtils.getRootCause(e).getMessage();
            // Fields with errors.
            assertTrue(message.contains("'network'"));
            assertTrue(message.contains("null"));
            assertFalse(message.contains("Invalid network (must be something like base-sepolia)"));
            // Fields without errors.
            assertFalse(message.contains("'defaultReceiverAddress'"));
            assertFalse(message.contains("'facilitatorUrl'"));
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
        } catch (ConfigurationPropertiesBindException e) {
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
