package tech.mogami.spring.autoconfigure.util.validators;

import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Blockchain network validator.
 */
@Documented
@Constraint(validatedBy = BlockchainNetworkValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface BlockchainNetwork {

    /**
     * Message to show when the validation fails.
     *
     * @return message
     */
    String message();

    /**
     * Groups.
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload.
     *
     * @return payload
     */
    Class<?>[] payload() default {};

}
