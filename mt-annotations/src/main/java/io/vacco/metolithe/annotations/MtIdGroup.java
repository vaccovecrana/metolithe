package io.vacco.metolithe.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To be placed on attributes which generate a unique id for
 * an entity. Target generated ids can be obtained by a generator
 * implementation class by reading attributes obtained inside
 * a) a group number and b) a position of that particular attribute
 * within the group.
 *
 * TODO document this in more detail
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MtIdGroup {
  int number();
  int position();
}
