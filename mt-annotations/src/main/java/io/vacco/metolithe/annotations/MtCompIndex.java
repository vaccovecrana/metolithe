package io.vacco.metolithe.annotations;

import java.lang.annotation.*;

/**
 * Column names will be grouped by the assigned index <code>name</code>
 * and order will be imposed according to <code>idx</code>.
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MtCompIndex {
  String name();
  int idx();
}
