package io.vacco.metolithe.core;

import io.vacco.metolithe.extraction.EnumExtractor;
import io.vacco.metolithe.spi.MtIdGenerator;
import io.vacco.metolithe.util.TypeUtil;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.*;
import org.codejargon.fluentjdbc.api.query.Mapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.*;
import static java.util.Objects.*;

public abstract class BaseQueryFactory<T, K> {

  private Class<T> clazz;
  private FluentJdbc jdbc;
  private Map<String, String> queryCache = new ConcurrentHashMap<>();
  private String sourceSchema;
  private EntityDescriptor<T> descriptor;
  private ObjectMappers objectMappers;

  private final Map<Class, ObjectMapperRsExtractor> extractors = new ConcurrentHashMap<>();
  private final MtIdGenerator<K> generator;

  public BaseQueryFactory(Class<T> clazz, FluentJdbc jdbc, String sourceSchema,
                          EntityDescriptor.CaseFormat format, MtIdGenerator<K> idGenerator) {
    this.clazz = requireNonNull(clazz);
    this.jdbc = requireNonNull(jdbc);
    this.sourceSchema = requireNonNull(sourceSchema);
    this.generator = requireNonNull(idGenerator);
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
    this.objectMappers = ObjectMappers.builder().extractors(extractors).build();
    this.descriptor = new EntityDescriptor<>(clazz, format);
    Class<?> idClass = TypeUtil.toWrapperClass(idGenerator.defaultValue().getClass());
    Class<?> entityPkClass = TypeUtil.toWrapperClass(descriptor.getField(descriptor.getPrimaryKeyField()).getType());
    if (!idClass.isAssignableFrom(entityPkClass)) {
      String msg = "Primary key field for target class [%s] with primary key field of type [%s] does not match Id generator class [%s]";
      throw new IllegalArgumentException(String.format(msg, clazz, idClass, entityPkClass));
    }
  }

  public K idOf(T target) {
    requireNonNull(target);
    K currentPk = getDescriptor().extract(target, getDescriptor().getPrimaryKeyField());
    boolean isFixed = getDescriptor().isFixedPrimaryKey();
    boolean isDefault = generator.defaultValue().equals(currentPk);
    boolean isNull = currentPk == null;
    if (isFixed && !isDefault && !isNull) { return currentPk; }
    Object [] pkValues = getDescriptor().extractPkComponents(target);
    Object pkVal = generator.apply(pkValues);
    return (K) pkVal;
  }

  public T setId(T target) {
    try {
      K id = idOf(target);
      getDescriptor().getField(getDescriptor().getPrimaryKeyField()).set(target, id);
      return target;
    } catch (Exception e) {
      throw new IllegalStateException(String.format(
          "Unable to generate/assign primary key generated value on [%s], target [%s]",
          clazz.getCanonicalName(), target), e);
    }
  }

  public FluentJdbc sql() { return jdbc; }

  public Mapper<T> mapTo(Class<T> targetBeanClass) {
    return objectMappers.forClass(targetBeanClass);
  }

  public Mapper<T> mapToDefault() { return mapTo(clazz); }

  public <J> Mapper<J> mapperFor(Class<J> targetClass) {
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
