package io.vacco.mt.test;

import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.id.MtXxHashLFn;
import io.vacco.mt.test.schema.DbUser;
import io.vacco.mt.test.schema.Device;
import io.vacco.mt.test.schema.Phone;
import io.vacco.mt.test.schema.TransientSchema;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import java.util.stream.Stream;

import static io.vacco.metolithe.core.MtCaseFormat.*;
import static io.vacco.shax.logging.ShArgument.kv;
import static j8spec.J8Spec.describe;
import static j8spec.J8Spec.it;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtAnnotationsTest extends MtTest {

  private static <T> void logDescriptor(MtDescriptor<T> d, T data) {
    var enums0 = d.getEnumFields();
    var comps0 = d.getPkValues(data);

    log.info("{}", kv("enums", enums0));
    log.info("{}", kv("comps", comps0));
    log.info("{}", propNames(d, true));
    log.info("{}", propNames(d, false));
    log.info("{}", propNamesCsv(d, true, "t0"));
    log.info("{}", propNamesCsv(d, false, "t0"));
    log.info("{}", placeholderCsv(d, true));
    log.info("{}", placeholderCsv(d, false));
    log.info("{}", placeHolderAssignmentCsv(d, true));
    log.info("{}", placeHolderAssignmentCsv(d, false));
  }

  static {
    describe("Annotations", () -> {
      it("Can describe annotated entities",
        () -> Stream.of(testSchema)
          .map(clazz -> new MtDescriptor<>(clazz, LOWER_CASE))
          .forEach(d -> log.info(d.toString()))
      );
      it("Can extract primary key components from entities", () -> {
        logDescriptor(new MtDescriptor<>(DbUser.class, UPPER_CASE), u0);
        logDescriptor(new MtDescriptor<>(Device.class, LOWER_CASE), d0);
        logDescriptor(new MtDescriptor<>(Phone.class, KEEP_CASE), p0);
        log.info("XXHashL: {}", new MtXxHashLFn().apply(new Object[]{"Hello", "friend"}));
      });
      it("Filters transient fields", () -> {
        var descriptor = new MtDescriptor<>(TransientSchema.class, MtCaseFormat.KEEP_CASE);
        // Should only have 2 fields: id and name. 'secret' should be ignored.
        assertEquals(2, descriptor.getFields(true).size());
        boolean secretFound = descriptor.getFields(true)
          .stream()
          .anyMatch(fd -> fd.getFieldName().equals("secret"));
        assertFalse("Transient field 'secret' should not be in descriptor", secretFound);
      });
      it("Supports nullable columns in composite unique constraints (#38)", () -> {
        var d = new MtDescriptor<>(io.vacco.mt.test.schema.KeyNamespace.class, MtCaseFormat.KEEP_CASE);
        var unqs = d.getUniqueConstraints();
        assertFalse(unqs.isEmpty());
        // nsId is nullable but part of unique idx=1
      });
    });
  }
}
