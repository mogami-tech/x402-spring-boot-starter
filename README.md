# Mogami x402 Spring Boot Starter

## Quick start

The Mogami x402 Spring Boot Starter adds a micropayment guard to any Spring Boot 3.2+ endpoint. Annotate a controller
with @X402PaymentRequirement, set your payment information, done!
Here is how to do it:
```java

@X402PaymentRequirement(
        scheme = "exact",
        network = "base-sepolia",
        maximumAmountRequired = "1000",
        payTo = "0x7553F6FA4Fb62986b64f79aEFa1fB93ea64A22b1",
        asset = "0x036CbD53842c5426634e7929541eC2318f3dCF7e",
        extra = {
                @X402PaymentRequirement.ExtraEntry(key = "name", value = "USDC"),
                @X402PaymentRequirement.ExtraEntry(key = "version", value = "2")
        })
@GetMapping("/weather")
public String weather() {
    return "It's sunny!";
}
```

To use it in your Maven project :
```xml

<dependency>
    <groupId>tech.mogami.spring</groupId>
    <artifactId>mogami-x402-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

and in your Gradle project:
```groovy
dependencies {
    implementation 'tech.mogami.spring:mogami-x402-spring-boot-starter:0.0.1'
}
```

## Sample application

TODO Write something here.