package tech.mogami.spring.autoconfigure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface X402ExactSchemes {

    X402ExactScheme[] value();

}
