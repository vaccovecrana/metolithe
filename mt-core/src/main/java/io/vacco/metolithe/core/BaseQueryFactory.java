package io.vacco.metolithe.core;

import io.vacco.metolithe.spi.MtCodec;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.*;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.internal.mappers.DefaultObjectMapperRsExtractors;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.*;
import static java.util.Objects.*;

public abstract class BaseQueryFactory<T> {

  private FluentJdbc jdbc;
  private Map<String, String> queryCache = new ConcurrentHashMap<>();
  private MtCodec codec;
  private String sourceSchema;
  private EntityDescriptor<T> descriptor;

  private ObjectMappers objectMappers;
  private final Map<Class, ObjectMapperRsExtractor> defaultExtractors = DefaultObjectMapperRsExtractors.extractors();
  private final Map<Class, ObjectMapperRsExtractor> extractors = new ConcurrentHashMap<>();

  protected abstract Class<T> getTargetClass();
  protected abstract Collection<Class<? extends Enum>> getEnumClasses();

  public BaseQueryFactory(FluentJdbc jdbc, MtCodec codec, String sourceSchema, EntityDescriptor.CaseFormat format) {
    this.jdbc = requireNonNull(jdbc);
    this.codec = requireNonNull(codec);
    this.sourceSchema = requireNonNull(sourceSchema);
    getEnumClasses().forEach(eClass -> extractors.put(eClass, new EnumExtractor(eClass)));
    extractors.put(Set.class, (rs, i) -> deSerialize(rs.getString(i)));
    objectMappers = ObjectMappers.builder().extractors(extractors).build();
    descriptor = new EntityDescriptor<>(getTargetClass(), format);
  }

  protected FluentJdbc sql() { return jdbc; }
  protected Mapper<T> mapTo(Class<T> targetBeanClass) { return objectMappers.forClass(targetBeanClass); }
  protected Mapper<T> mapToDefault() { return mapTo(getTargetClass()); }
  public <K> Mapper<K> mapperFor(Class<K> targetClass) { return objectMappers.forClass(targetClass); }

  protected Object deSerialize(String input) {
    if (input != null) return codec.decode(input);
    return null;
  }

  protected Object serialize(Object payload) {
    if (payload != null &&
        !defaultExtractors.containsKey(payload.getClass()) &&
        !extractors.containsKey(payload.getClass())) {
      return codec.encode(payload, payload.getClass());
    }
    return payload;
  }

  protected String classError(Enum<?> type) {
    return format("%s.%s", type, getTargetClass().getSimpleName().toLowerCase());
  }

  protected String getSchemaName(Class<?> clazz) {
    String tableName = clazz.getSimpleName()
        .replaceAll("(.)(\\p{Upper})", "$1_$2")
        .toLowerCase();
    return format("%s.%s", sourceSchema, tableName);
  }

  protected String getSchemaName() { return getSchemaName(getTargetClass()); }
  protected Map<String, String> getQueryCache() { return queryCache; }
  protected EntityDescriptor<T> getDescriptor() { return descriptor; }
  protected String getPrimaryKeyId() { return descriptor.getPrimaryKeyField(); }
}
