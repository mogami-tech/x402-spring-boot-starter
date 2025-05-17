package tech.mogami.spring.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mogami.spring.autoconfigure.annotation.X402ExactScheme;

import static tech.mogami.spring.autoconfigure.util.constants.networks.BaseNetworks.BASE_SEPOLIA;
import static tech.mogami.spring.test.constants.TestData.ASSET_CONTRACT_ADDRESS;
import static tech.mogami.spring.test.constants.TestData.SERVER_ADDRESS;

@RestController
public class WeatherController {

    @GetMapping("/weather/without-payment")
    public String weatherWithoutPayment() {
        return "It's rainy!";
    }

    @X402ExactScheme(
            network = BASE_SEPOLIA,
            payTo = SERVER_ADDRESS,
            asset = ASSET_CONTRACT_ADDRESS,
            maximumAmountRequired = "1000"
    )
    @GetMapping("/weather")
    public String weather() {
        return "It's sunny!";
    }

}
