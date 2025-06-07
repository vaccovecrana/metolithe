package io.vacco.mt.test.annotations;

import io.vacco.metolithe.annotations.MtNotNull;
import io.vacco.metolithe.annotations.MtVarchar;
import java.lang.annotation.*;

@MtNotNull
@MtVarchar(32)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface St32 {}
