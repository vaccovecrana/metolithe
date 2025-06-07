package io.vacco.mt.test.schema;

import io.vacco.metolithe.annotations.*;
import io.vacco.mt.test.annotations.St16;

@MtEntity public class Phone {

  @MtPk public int pid;

  @MtField @MtNotNull
  @MtIndex @MtUnique(idx = 0, inPk = true)
  public int countryCode;

  @St16
  @MtIndex @MtUnique(idx = 1, inPk = true)
  public String number;

  @MtField public int smsVerificationCode;
}
