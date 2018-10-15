package io.vacco.metolithe.core;

import io.vacco.metolithe.extraction.EnumExtractor;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.*;
import org.codejargon.fluentjdbc.api.query.Mapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.*;
import static java.util.Objects.*;

public abstract class BaseQueryFactory<T> {

  private Class<T> clazz;
  private FluentJdbc jdbc;
  private Map<String, String> queryCache = new ConcurrentHashMap<>();
  private String sourceSchema;
  private EntityDescriptor<T> descriptor;

  private ObjectMappers objectMappers;
  private final Map<Class, ObjectMapperRsExtractor> extractors = new ConcurrentHashMap<>();

  public BaseQueryFactory(Class<T> clazz, FluentJdbc jdbc, String sourceSchema, EntityDescriptor.CaseFormat format) {
    this.clazz = requireNonNull(clazz);
    this.jdbc = requireNonNull(jdbc);
    this.sourceSchema = requireNonNull(sourceSchema);
    for (Field fld : clazz.getDeclaredFields()) {
      if (fld.getType().isEnum()) {
        extractors.put(fld.getType(), new EnumExtractor(fld.getType()));
      } else if (Collection.class.isAssignableFrom(fld.getType())) {
        String msg = String.join("\n",
            "Cannot map collection fields for class [%s], field [%s].",
            "Use a one-to-many/many-to-many entity instead.");
        throw new IllegalArgumentException(String.format(msg, clazz.getCanonicalName(), fld));
      }
    }
    objectMappers = ObjectMappers.builder().extractors(extractors).build();
    descriptor = new EntityDescriptor<>(clazz, format);
  }

  public FluentJdbc sql() { return jdbc; }

  public Mapper<T> mapTo(Class<T> targetBeanClass) {
    return objectMappers.forClass(targetBeanClass);
  }

  public Mapper<T> mapToDefault() { return mapTo(clazz); }

  public <K> Mapper<K> mapperFor(Class<K> targetClass) {
    return objectMappers.forClass(targetClass);
  }

  public String getSchemaName() { return getSchemaName(clazz); }

  public EntityDescriptor<T> getDescriptor() { return descriptor; }

  protected String classError(Enum<?> type) {
    return format("%s.%s", type, clazz.getSimpleName().toLowerCase());
  }

  protected String getSchemaName(Class<?> clazz) {
    String tableName = clazz.getSimpleName().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    return format("%s.%s", sourceSchema, tableName);
  }

  protected Map<String, String> getQueryCache() { return queryCache; }
}
