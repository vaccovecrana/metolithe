package io.vacco.metolithe.test;

import io.vacco.metolithe.core.*;
import io.vacco.metolithe.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import org.slf4j.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static j8spec.J8Spec.*;
import static io.vacco.shax.logging.ShArgument.*;
import static io.vacco.metolithe.core.MtCaseFormat.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtAnnotationsSpec extends MtSpec {

  private static final Logger log = LoggerFactory.getLogger(MtAnnotationsSpec.class);

  static {

    User u0 = new User();
    u0.alias = "Jane";
    u0.email = "jane@me.com";
    u0.pw = "0xAAABBBCCCDDDEEFFF";
    u0.uid = 112233;

    Phone p0 = new Phone();
    p0.countryCode = 1;
    p0.number = "5555555555";
    p0.smsVerificationCode = 1234;

    Device d0 = new Device();
    d0.type = Device.DType.ANDROID;
    d0.signingKey = "-----BEGIN PUBLIC KEY-----\n" +
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvjvvabCydtvNmDPrHNCQypr1/\n" +
        "BjT54hwLXbuA8rnDmZOO5o/M5+FgODHpSS/h1IsvC7SxKkHHgYwTE4SuJb7umqwx\n" +
        "M8Yvnv4jRyTwM71/kDaUBs+xHoKBk2A1qn8oOW8Ub/7wP2/o6YHtuerAkEIM5jBN\n" +
        "Et1+/x6vjLwCEix44QIDAQAB\n" +
        "-----END PUBLIC KEY-----\n";

    describe("MT annotations", () -> {
      it("Can describe annotated entities",
          () -> Stream.of(testSchema)
              .map(clazz -> new MtDescriptor<>(clazz, LOWER_CASE))
              .forEach(d -> log.info(d.toString()))
      );
      it("Can extract primary key components from entities", () -> {
        logDescriptor(new MtDescriptor<>(User.class, UPPER_CASE), u0, log);
        logDescriptor(new MtDescriptor<>(Device.class, LOWER_CASE), d0, log);
        logDescriptor(new MtDescriptor<>(Phone.class, KEEP_CASE), p0, log);
      });
    });
  }

  private static <T> void logDescriptor(MtDescriptor<T> d, T data, Logger log) {
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
}
