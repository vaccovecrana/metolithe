package io.vacco.mt.test;

import io.vacco.metolithe.dao.MtDaoMapper;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import java.io.File;

import static io.vacco.mt.test.MtTest.*;
import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtCodegenTest {

  static {
    it("Generates typed field DAO definitions", () -> {
      var out = new File(".", "src/main/java");
      new MtDaoMapper().mapSchema(out, "io.vacco.mt.test.dao", fmt, testSchema);
    });
  }

}
