package tech.mogami.spring.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mogami.spring.autoconfigure.annotation.X402PaymentRequirements;

import static tech.mogami.commons.constants.networks.BaseNetworks.BASE_SEPOLIA;
import static tech.mogami.commons.header.payment.schemes.ExactSchemeConstants.EXACT_SCHEME_NAME;
import static tech.mogami.spring.test.util.TestData.ASSET_CONTRACT_ADDRESS;
import static tech.mogami.spring.test.util.TestData.SERVER_WALLET_ADDRESS_1;
import static tech.mogami.spring.test.util.TestData.SERVER_WALLET_ADDRESS_2;

@SuppressWarnings("SameReturnValue")
@RestController
public class WeatherController {

    @GetMapping("/weather/without-payment")
    public String weatherWithoutPayment() {
        return "It's rainy!";
    }

    @X402PaymentRequirements(
            scheme = EXACT_SCHEME_NAME,
            network = BASE_SEPOLIA,
            maximumAmountRequired = "1000",
            payTo = SERVER_WALLET_ADDRESS_1,
            asset = ASSET_CONTRACT_ADDRESS,
            extra = {
                    @X402PaymentRequirements.ExtraEntry(key = "name", value = "USDC"),
                    @X402PaymentRequirements.ExtraEntry(key = "version", value = "2")
            }
    )
    @X402PaymentRequirements(
            scheme = EXACT_SCHEME_NAME,
            network = BASE_SEPOLIA,
            maximumAmountRequired = "2000",
            description = "Description number 2",
            payTo = SERVER_WALLET_ADDRESS_2,
            asset = ASSET_CONTRACT_ADDRESS
    )
    @GetMapping("/weather")
    public String weather() {
        return "It's sunny!";
    }

}
