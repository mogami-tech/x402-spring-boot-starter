<p align="center">
    <a href="https://mogami.gitbook.io/mogami/spring-boot-starter/get-started">Quick Start</a> | 
    <a href="https://mogami.gitbook.io/mogami">Documentation</a> | 
    <a href="https://github.com/mogami-tech/x402-examples">Examples</a> | 
    <a href="https://x.com/mogami_tech">Twitter</a>
</p>

<p align="center">
    <a href="https://mogami.gitbook.io/mogami/spring-boot-starter/get-started">
        <img    src="https://mogami.tech/images/logo/logo_mogami_vertical_small.png"
                alt="Mogami logo"/>
    </a>
</p>

<hr>

<h3 align="center">Mogami X402 spring boot starter — one annotation, instant paywall.</h2>
<br>

The Mogami x402 Spring Boot Starter adds a micropayment guard to any Spring Boot endpoint.
Annotate your controller method with `@X402PaymentRequirement`, set your payment information, done!

Here is how to do it:
```java

@X402PaymentRequirements(
        scheme = "exact",
        network = "base-sepolia",
        maximumAmountRequired = "1000",
        payTo = "0x7553F6FA4Fb62986b64f79aEFa1fB93ea64A22b1",
        asset = "0x036CbD53842c5426634e7929541eC2318f3dCF7e",
        extra = {
                @X402PaymentRequirements.ExtraEntry(key = "name", value = "USDC"),
                @X402PaymentRequirements.ExtraEntry(key = "version", value = "2")
        })
@GetMapping("/weather")
public String weather() {
    return "It's sunny!";
}
```

<p align="center">
    <a href="https://mogami.gitbook.io/mogami/spring-boot-starter/get-started">Quick Start</a> | 
    <a href="https://mogami.gitbook.io/mogami">Documentation</a> | 
    <a href="https://github.com/mogami-tech/x402-examples">Examples</a> | 
    <a href="https://x.com/mogami_tech">Twitter</a>
</p>