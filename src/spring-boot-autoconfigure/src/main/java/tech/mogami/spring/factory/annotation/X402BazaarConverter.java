package tech.mogami.spring.factory.annotation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import tech.mogami.commons.constant.BodyType;
import tech.mogami.commons.constant.HttpMethod;
import tech.mogami.commons.payment.extensions.bazaar.BazaarExtension;
import tech.mogami.commons.payment.extensions.bazaar.BazaarInfo;
import tech.mogami.commons.payment.extensions.bazaar.BazaarInput;
import tech.mogami.commons.payment.extensions.bazaar.BazaarOutput;
import tech.mogami.commons.util.JsonUtil;
import tech.mogami.spring.annotation.X402Bazaar;

import java.util.Arrays;

/**
 * Converts an {@link X402Bazaar} annotation into a {@link BazaarExtension}.
 */
@UtilityClass
@SuppressWarnings("HideUtilityClassConstructor")
public class X402BazaarConverter {

    /**
     * Converts the given {@link X402Bazaar} annotation to a {@link BazaarExtension}.
     *
     * @param annotation the annotation to convert
     * @return the BazaarExtension built from the annotation
     */
    public static BazaarExtension convert(final X402Bazaar annotation) {
        final BazaarInfo.BazaarInfoBuilder infoBuilder = BazaarInfo.builder()
                .input(buildInput(annotation));

        if (StringUtils.isNotBlank(annotation.outputType())) {
            infoBuilder.output(buildOutput(annotation));
        }

        final BazaarExtension.BazaarExtensionBuilder builder = BazaarExtension.builder()
                .info(infoBuilder.build());

        if (StringUtils.isNotBlank(annotation.schema())) {
            builder.schema(JsonUtil.fromJson(annotation.schema(), JsonNode.class));
        }

        return builder.build();
    }

    /**
     * Builds a {@link BazaarInput} from the annotation fields.
     *
     * @param annotation the annotation to convert
     * @return the BazaarInput
     */
    private static BazaarInput buildInput(final X402Bazaar annotation) {
        final BazaarInput.BazaarInputBuilder inputBuilder = BazaarInput.builder()
                .type("http")
                .method(resolveHttpMethod(annotation.inputMethod()));

        if (StringUtils.isNotBlank(annotation.inputBodyType())) {
            inputBuilder.bodyType(resolveBodyType(annotation.inputBodyType()));
        }

        if (StringUtils.isNotBlank(annotation.inputBody())) {
            inputBuilder.body(JsonUtil.fromJson(annotation.inputBody(), JsonNode.class));
        }

        return inputBuilder.build();
    }

    /**
     * Builds a {@link BazaarOutput} from the annotation fields.
     *
     * @param annotation the annotation to convert
     * @return the BazaarOutput
     */
    private static BazaarOutput buildOutput(final X402Bazaar annotation) {
        final BazaarOutput.BazaarOutputBuilder outputBuilder = BazaarOutput.builder()
                .type(annotation.outputType());

        if (StringUtils.isNotBlank(annotation.outputExample())) {
            outputBuilder.example(JsonUtil.fromJson(annotation.outputExample(), JsonNode.class));
        }

        return outputBuilder.build();
    }

    /**
     * Resolves a body type string to a {@link BodyType} enum constant.
     *
     * @param bodyType the body type string (e.g., "json", "form-data", "text")
     * @return the matching BodyType enum constant
     * @throws IllegalArgumentException if the body type is not recognized
     */
    private static BodyType resolveBodyType(final String bodyType) {
        return Arrays.stream(BodyType.values())
                .filter(bt -> bt.value().equalsIgnoreCase(bodyType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown body type: " + bodyType));
    }

    /**
     * Resolves an HTTP method string to an {@link HttpMethod} enum constant.
     *
     * @param method the HTTP method string (e.g., "GET", "POST")
     * @return the matching HttpMethod enum constant
     * @throws IllegalArgumentException if the method is not recognized
     */
    private static HttpMethod resolveHttpMethod(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(m -> m.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown HTTP method: " + method));
    }

}
