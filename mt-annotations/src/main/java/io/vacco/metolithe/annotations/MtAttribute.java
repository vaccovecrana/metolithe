package io.vacco.metolithe.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To be placed on a property which must be persisted,
 * or an aggregator annotation for a property field.
 *
 * @since 0.10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface MtAttribute {
  boolean nil() default true;
  int len() default -1;
}
