package io.vacco.metolithe.annotations;

import java.lang.annotation.*;

/**
 * To be placed on a property field intended to be an index.
 *
 * @since 0.10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface MtIndex {}
