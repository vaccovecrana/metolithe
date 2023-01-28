package io.vacco.metolithe.core;

import io.vacco.metolithe.annotations.MtPk;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.*;
import org.codejargon.fluentjdbc.api.query.Mapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MtDao<T, K> {

  private final String schemaName;
  private final FluentJdbc jdbc;
  private final ObjectMappers mappers;

  protected final MtDescriptor<T> dsc;
  protected final MtIdFn<K> idFn;

  protected final Map<Class, ObjectMapperRsExtractor> extractors = new ConcurrentHashMap<>();
  protected final Map<String, String> queryCache = new ConcurrentHashMap<>();

  public MtDao(String schemaName, FluentJdbc jdbc, MtDescriptor<T> d, MtIdFn<K> idFn) {
    this.schemaName = requireNonNull(schemaName);
    this.jdbc = requireNonNull(jdbc);
    this.dsc = requireNonNull(d);
    this.idFn = requireNonNull(idFn);

    d.getEnumFields().forEach(fld -> extractors.put(fld, new MtEnumExtractor(fld)));
    this.mappers = ObjectMappers.builder().extractors(extractors).build();

    var opk = d.get(MtPk.class).findFirst();
    if (opk.isPresent()) {
      var idFnClass = idFn.getIdType();
      var entityPkClass = MtUtil.toWrapperClass(opk.get().getType());
      if (!idFnClass.isAssignableFrom(entityPkClass)) {
        throw new MtException.MtIdGeneratorMismatchException(d.getClassName(), entityPkClass, idFnClass);
      }
    }
  }

  public FluentJdbc sql() {
    return jdbc;
  }

  public Mapper<T> mapToDefault() {
    return this.mappers.forClass(this.dsc.getType());
  }

  public ObjectMappers getMappers() {
    return mappers;
  }

  public String getSchemaName() {
    return getSchemaName(this.dsc.getType());
  }

  protected String getSchemaName(Class<?> clazz) {
    var raw = String.format("%s.%s", schemaName, dsc.getFormat().of(clazz.getSimpleName()));
    return dsc.getFormat().of(raw);
  }

  protected Map<String, String> getQueryCache() {
    return queryCache;
  }
}

