package tech.mogami.spring.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mogami.spring.autoconfigure.annotation.X402;

import static tech.mogami.spring.test.constants.TestData.ASSET_CONTRACT_ADDRESS;
import static tech.mogami.spring.test.constants.TestData.SERVER_ADDRESS;

@RestController
public class WeatherController {


    @GetMapping("/weather/without-payment")
    public String weatherWithoutPayment() {
        return "It's rainy!";
    }

    @X402(
            payTo = SERVER_ADDRESS,
            asset = ASSET_CONTRACT_ADDRESS,
            maximumAmountRequired = "1000",
            network = "base-sepolia"
    )
    @GetMapping("/weather")
    public String weather() {
        return "It's sunny!";
    }

}
