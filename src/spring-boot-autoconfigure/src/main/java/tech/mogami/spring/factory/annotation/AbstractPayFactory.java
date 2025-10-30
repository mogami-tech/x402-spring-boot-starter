package tech.mogami.spring.factory.annotation;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tech.mogami.spring.parameter.X402Parameters;

import java.lang.annotation.Annotation;

/**
 * Abstract implementation of PayFactory that provides a default behavior.
 *
 * @param <A> the annotation type handled by this factory
 */
@Slf4j
public abstract class AbstractPayFactory<A extends Annotation> implements PayFactory<A> {

    /** X402 parameters. */
    @Setter
    protected X402Parameters x402Parameters;

}
