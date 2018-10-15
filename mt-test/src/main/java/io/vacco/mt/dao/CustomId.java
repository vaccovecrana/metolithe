package io.vacco.mt.dao;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtId;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@MtId @MtAttribute(len = 16)
public @interface CustomId {}
