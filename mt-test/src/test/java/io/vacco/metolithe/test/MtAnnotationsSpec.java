package io.vacco.metolithe.test;

import io.vacco.metolithe.core.*;
import io.vacco.metolithe.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static j8spec.J8Spec.*;
import static io.vacco.shax.logging.ShArgument.*;
import static io.vacco.metolithe.core.MtCaseFormat.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtAnnotationsSpec extends MtSpec {

  private static <T> void logDescriptor(MtDescriptor<T> d, T data) {
    List<Class<Enum<?>>> enums0 = d.getEnumFields();
    Object[] comps0 = d.getPkValues(data);
    Map<String, Object> allComps = d.getAll(data);

    log.info("{}", kv("enums", enums0));
    log.info("{}", kv("comps", comps0));
    log.info("{}", kv("allComps", allComps));

    log.info("{}", propNames(d, true));
    log.info("{}", propNames(d, false));
    log.info("{}", propNamesCsv(d, true));
    log.info("{}", propNamesCsv(d, false));
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
      });
    });
  }
}
