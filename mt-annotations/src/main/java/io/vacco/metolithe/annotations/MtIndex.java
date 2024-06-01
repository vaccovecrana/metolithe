package io.vacco.metolithe.annotations;

import java.lang.annotation.*;

/**
 * Column names will be grouped by the assigned index <code>name</code>
 * and order will be imposed according to <code>idx</code>.
 * If none are specified, the index will be created considering only the
 * assigned class field.
 *
 * @since 2.9.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MtIndex {
  String name() default "";
  int idx() default -1;
}
