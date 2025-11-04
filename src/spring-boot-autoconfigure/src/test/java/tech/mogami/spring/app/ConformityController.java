package tech.mogami.spring.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mogami.commons.test.BaseTest;
import tech.mogami.spring.annotation.X402PayUSDC;

@SuppressWarnings("SameReturnValue")
@RestController
public class ConformityController extends BaseTest {

    @X402PayUSDC(
            amount = "0.01",
            payTo = "0x209693Bc6afc0C5328bA36FaF03C514EF312287C",
            network = "base-sepolia",
            description = "Access to protected content",
            maximumTimeoutSeconds = 300,
            outputSchema = """
                    {
                      "input": {
                        "discoverable": true,
                        "method": "GET",
                        "type": "http"
                      }
                    }
                    """
    )
    @GetMapping("/protected")
    public String protectedService() {
        return "It's protected Stephane!";
    }

}
