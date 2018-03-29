package io.vacco.metolithe.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To be placed on a property which must be persisted.
 *
 * @since 0.10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MtAttribute {
  boolean nil() default true;
  long len() default -1;
}
