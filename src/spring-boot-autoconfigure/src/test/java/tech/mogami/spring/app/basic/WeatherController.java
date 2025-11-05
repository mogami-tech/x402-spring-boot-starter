package tech.mogami.spring.app.basic;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mogami.commons.test.BaseTest;
import tech.mogami.spring.annotation.X402PayUSDC;
import tech.mogami.spring.annotation.X402PaymentRequirements;

import static tech.mogami.commons.constant.network.base.BaseContracts.BASE_SEPOLIA_USDC_CONTRACT;

@SuppressWarnings("SameReturnValue")
@RestController
public class WeatherController extends BaseTest {

    @GetMapping("/weather/without-payment")
    public String weatherWithoutPayment() {
        return "It's rainy!";
    }

    // X402PaymentRequirements annotation ==============================================================================

    @X402PaymentRequirements(
            scheme = "exact",
            network = "base-sepolia",
            maximumAmountRequired = "1000",
            payTo = TEST_SERVER_WALLET_ADDRESS_1,
            asset = BASE_SEPOLIA_USDC_CONTRACT,
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
            asset = BASE_SEPOLIA_USDC_CONTRACT
    )
    @GetMapping("/weather")
    public String weather() {
        return "It's sunny!";
    }

    // X402PayUSDC annotation ==========================================================================================

    @X402PayUSDC(amount = "3.6")
    @X402PayUSDC(
            amount = "5.2",
            payTo = "0x71C7656EC7ab88b098defB751B7401B5f6d8976H",
            network = "base",
            description = "Complex payment"
    )
    @GetMapping("/weatherWithX402PayUSDC")
    public String weatherWithX402PayUSDC() {
        return "It's sunny with X402PayUSDC!";
    }

}
