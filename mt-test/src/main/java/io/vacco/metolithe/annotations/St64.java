package io.vacco.metolithe.annotations;

import java.lang.annotation.*;

@MtNotNull
@MtVarchar(64)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface St64 {}
