package io.vacco.metolithe.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MtDao {
  boolean loadEq() default false;
  boolean loadIn() default false;
  boolean deleteEq() default false;
}
