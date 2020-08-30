package io.vacco.metolithe.test;

import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.schema.*;
import io.vacco.shax.logging.ShOption;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import org.slf4j.*;

import java.util.stream.Stream;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtAnnotationsSpec {
  static {

    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_DEVMODE, "true");
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_PRETTYPRINT, "true");

    final Logger log = LoggerFactory.getLogger(MtAnnotationsSpec.class);

    describe("Metolithe annotations", () -> {
      it("Can describe annotated entities", () -> {
        Stream.of(
            Device.class, DeviceLocation.class, DeviceTag.class,
            Phone.class, User.class, UserFollow.class
        ).map(MtDescriptor::new).forEach(d -> log.info(d.toString()));
      });
    });
  }
}
