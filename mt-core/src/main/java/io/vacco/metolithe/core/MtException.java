package io.vacco.metolithe.core;

import java.util.List;

public class MtException {

  private static final String UNKNOWN = "unknown";

  public static class MtForeignKeyMismatchException extends RuntimeException {
    public final String src, srcField, srcFieldType;
    public final String dst, dstField, dstFieldType;
    public MtForeignKeyMismatchException(String src, String srcField, String srcFieldType,
                                         String dst, String dstField, String dstFieldType) {
      this.src = src;
      this.srcField = srcField;
      this.srcFieldType = srcFieldType;
      this.dst = dst;
      this.dstField = dstField;
      this.dstFieldType = dstFieldType;
    }
  }

  public static class MtMultiplePkDefinitionsException extends RuntimeException {
    public final List<MtFieldDescriptor> keys;
    public MtMultiplePkDefinitionsException(List<MtFieldDescriptor> keys) {
      this.keys = keys;
    }
  }

  public static class MtMissingPkComponentException extends RuntimeException {
    public final Object src;
    public final MtFieldDescriptor componentField;
    public MtMissingPkComponentException(Object src, MtFieldDescriptor compField) {
      this.src = src;
      this.componentField = compField;
    }
  }

  public static class MtIdGeneratorMismatchException extends RuntimeException {

    public final String targetClass;
    public final String pkFieldType;
    public final String idGenType;

    public MtIdGeneratorMismatchException(String targetClassName, Class<?> pkFieldType, Class<?> idGenType) {
      this.targetClass = targetClassName;
      this.pkFieldType = pkFieldType.getCanonicalName();
      this.idGenType = idGenType.getCanonicalName();
    }
  }

  public static class MtPrimitiveMappingException extends RuntimeException {
    public final String sourceType;
    public MtPrimitiveMappingException(Class<?> type) {
      this.sourceType = type != null ? type.getCanonicalName() : UNKNOWN;
    }
  }

  public static class MtSqlTypeMappingException extends RuntimeException {
    public final MtFieldDescriptor descriptor;
    public MtSqlTypeMappingException(MtFieldDescriptor fd) {
      this.descriptor = fd;
    }
  }

  public static class MtEnumExtractionException extends RuntimeException {
    public final String target;
    public MtEnumExtractionException(Class<?> target, Exception e) {
      super(e);
      this.target = target != null ? target.getCanonicalName() : UNKNOWN;
    }
  }

  public static class MtMissingIdException extends RuntimeException {
    public MtMissingIdException(Object field) {
      super(field == null ? UNKNOWN : field.toString());
    }
  }

  public static class MtAccessException extends RuntimeException {
    public MtAccessException(Object target) {
      super(target == null ? UNKNOWN : target.toString());
    }
  }
}
