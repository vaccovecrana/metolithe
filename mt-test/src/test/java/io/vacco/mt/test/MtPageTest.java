package io.vacco.mt.test;

import io.vacco.metolithe.util.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import java.util.*;

import static j8spec.J8Spec.*;
import static org.junit.Assert.assertEquals;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtPageTest extends MtTest {
  static {
    it("Can create object pages", () -> {
      var nx1 = "Some other user name";
      var p1 = MtPage1.of(2, Arrays.asList(u0, u1), nx1);
      assertEquals(2, p1.size);
      assertEquals(2, p1.items.size());
      assertEquals(nx1, p1.nx1);

      var p2 = MtPage2.of(1, Collections.singletonList(u0), u1.alias, u1.email);
      assertEquals(1, p2.size);
      assertEquals(1, p2.items.size());
      assertEquals(u1.alias, p2.nx1);
      assertEquals(u1.email, p2.nx2);
    });
  }
}
