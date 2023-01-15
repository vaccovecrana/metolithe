package io.vacco.metolithe.core;

import java.io.File;
import java.util.List;

@SuppressWarnings("serial")
public class MtException {

  private static final String UNKNOWN = "unknown";

  public static class MtMissingFieldException extends RuntimeException {
    public String field;
    public MtDescriptor<?> descriptor;

    public MtMissingFieldException(String field, MtDescriptor<?> descriptor) {
      this.field = field;
      this.descriptor = descriptor;
    }
  }

  public static class MtPageAccessException extends RuntimeException {
    public String sortField;
    public Object indexPage;
    public MtDescriptor<?> descriptor;

    public MtPageAccessException(String sortField, Object indexPage,
                                 MtDescriptor<?> descriptor, Exception e) {
      super(e);
      this.sortField = sortField;
      this.indexPage = indexPage;
      this.descriptor = descriptor;
    }
  }

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

  public static class MtFieldAccessException extends RuntimeException {
    public Object target;
    public Object value;

    public MtFieldAccessException(Object target, Object value, Exception e) {
      super(e);
      this.target = target;
      this.value = value;
    }
  }

  public static class MtDaoMappingException extends RuntimeException {
    public final File outDir;
    public final String outPackage;
    public final Class<?>[] schemaClasses;

    public MtDaoMappingException(File outDir, String outPackage, Class<?>[] schemaClasses, Exception e) {
      super(e);
      this.outDir = outDir;
      this.outPackage = outPackage;
      this.schemaClasses = schemaClasses;
    }
  }
}
