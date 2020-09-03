package io.vacco.metolithe.test;

import io.vacco.metolithe.codegen.liquibase.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import org.slf4j.*;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtCodeGenSpec extends MtSpec {

  private static final Logger log = LoggerFactory.getLogger(MtAnnotationsSpec.class);

  static {
    describe("MT Schema code generation", () -> {
      it("Generates Liquibase changelogs", () -> {
        MtLbWriter.writeTo(
            new MtLbMapper().mapSchema(testSchema),
            System.out
        );
      });
    });
  }

}
