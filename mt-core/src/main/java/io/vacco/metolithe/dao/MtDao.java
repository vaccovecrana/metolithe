package io.vacco.metolithe.dao;

import io.vacco.metolithe.annotations.MtPk;
import io.vacco.metolithe.core.*;
import io.vacco.metolithe.id.MtIdFn;
import io.vacco.metolithe.query.*;
import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.vacco.metolithe.core.MtErr.*;
import static java.util.Objects.*;

public abstract class MtDao<T, K> implements MtMapper<T> {

  protected final String schema;
  protected final MtJdbc jdbc;
  public    final MtDescriptor<T> dsc;
  protected final MtIdFn<K> idFn;
  protected final Map<String, String> queryCache = new ConcurrentHashMap<>();
  private   final Constructor<T> constructor;

  public MtDao(String schema, MtJdbc jdbc, MtDescriptor<T> d, MtIdFn<K> idFn) {
    this.schema = requireNonNull(schema);
    this.jdbc = jdbc;
    this.dsc = requireNonNull(d);
    this.idFn = requireNonNull(idFn);
    var opk = d.get(MtPk.class).findFirst();
    if (opk.isPresent()) {
      var idFnClass = idFn.getIdType();
      var entityPkClass = MtUtil.toWrapperClass(opk.get().getType());
      if (!idFnClass.isAssignableFrom(entityPkClass)) {
        throw badIdGenerator(d.getClassName(), entityPkClass.getTypeName(), idFnClass.getTypeName());
      }
    }
    try {
      this.constructor = d.getType().getDeclaredConstructor();
    } catch (Exception e) {
      throw badConstructor(d, e);
    }
  }

  public static Class<?> toWrapperClass(Class<?> type) {
    if (!type.isPrimitive()) return type;
    if (int.class.equals(type))     { return Integer.class; }
    if (double.class.equals(type))  { return Double.class; }
    if (char.class.equals(type))    { return Character.class; }
    if (boolean.class.equals(type)) { return Boolean.class; }
    if (long.class.equals(type))    { return Long.class; }
    if (float.class.equals(type))   { return Float.class; }
    if (short.class.equals(type))   { return Short.class; }
    if (byte.class.equals(type))    { return Byte.class; }
    return type;
  }

  /**
   * This handles custom data storage formats from DB drivers.
   * Right now, we just handle:
   * <ul>
   *   <li>SQLite: Integer -> Boolean</li>
   * </ul>
   * many such cases...
   */
  private Object mapPrimitive(Object from, Class<?> to) {
    if (from != null) {
      if (from instanceof Integer && to.equals(Boolean.class)) {
        var i = (Integer) from;
        return i != 0;
      }
    }
    return from;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override public T map(ResultSet rs) throws SQLException {
    try {
      T instance = constructor.newInstance();
      var metadata = rs.getMetaData();
      for (int i = 1; i <= metadata.getColumnCount(); i++) {
        var val = rs.getObject(i);
        if (val != null) {
          var fd = dsc.getField(metadata.getColumnLabel(i));
          if (fd.isEnum()) {
            val = Enum.valueOf((Class<? extends Enum>) fd.getType(), val.toString());
          } else {
            var fdt = toWrapperClass(fd.getType());
            if (!fdt.isAssignableFrom(val.getClass())) {
              val = mapPrimitive(val, fdt);
            }
          }
          fd.setValue(instance, val);
        }
      }
      return instance;
    } catch (Exception e) {
      throw badExtraction(dsc, e);
    }
  }

  public MtJdbc sql() {
    return jdbc;
  }

  public MtMapper<T> mapToDefault() {
    return this;
  }

  public String getTableName() {
    return this.dsc.getTableName(schema);
  }

  protected Map<String, String> getQueryCache() {
    return queryCache;
  }

}

