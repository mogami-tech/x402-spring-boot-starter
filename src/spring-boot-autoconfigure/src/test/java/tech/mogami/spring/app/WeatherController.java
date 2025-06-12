package tech.mogami.spring.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mogami.commons.test.BaseTest;
import tech.mogami.spring.autoconfigure.annotation.X402PaymentRequirements;


@SuppressWarnings("SameReturnValue")
@RestController
public class WeatherController extends BaseTest {

    @GetMapping("/weather/without-payment")
    public String weatherWithoutPayment() {
        return "It's rainy!";
    }

    @X402PaymentRequirements(
            scheme = "exact",
            network = "base-sepolia",
            maximumAmountRequired = "1000",
            payTo = TEST_SERVER_WALLET_ADDRESS_1,
            asset = TEST_ASSET_CONTRACT_ADDRESS,
            extra = {
                    @X402PaymentRequirements.ExtraEntry(key = "name", value = "USDC"),
                    @X402PaymentRequirements.ExtraEntry(key = "version", value = "2")
            }
    )
    @X402PaymentRequirements(
            scheme = "exact",
            network = "base-sepolia",
            maximumAmountRequired = "2000",
            description = "Description number 2",
            payTo = TEST_SERVER_WALLET_ADDRESS_2,
            asset = TEST_ASSET_CONTRACT_ADDRESS
    )
    @GetMapping("/weather")
    public String weather() {
        return "It's sunny!";
    }

}
