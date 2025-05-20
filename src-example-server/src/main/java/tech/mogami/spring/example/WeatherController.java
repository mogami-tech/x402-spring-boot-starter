package tech.mogami.spring.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mogami.spring.autoconfigure.annotation.X402PaymentRequirement;

@RestController
public class WeatherController {

    @X402PaymentRequirement(
            scheme = "exact",
            network = "base-sepolia",
            maximumAmountRequired = "1000",
            payTo = "0x7553F6FA4Fb62986b64f79aEFa1fB93ea64A22b1",
            asset = "0x036CbD53842c5426634e7929541eC2318f3dCF7e",
            extra = {
                    @X402PaymentRequirement.ExtraEntry(key = "name", value = "USDC"),
                    @X402PaymentRequirement.ExtraEntry(key = "version", value = "2")
            }
    )
    @GetMapping("/weather")
    public WeatherResponse weather() {
        return new WeatherResponse(new WeatherResponse.Report("sunny", 25));
    }

}
