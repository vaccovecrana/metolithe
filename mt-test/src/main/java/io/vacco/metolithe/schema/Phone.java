package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class Phone {

  @MtPk public int pid;

  @MtField @MtNotNull
  @MtIndex @MtUnique(idx = 0, inPk = true)
  public int countryCode;

  @St16 @MtIndex @MtUnique(idx = 1, inPk = true)
  public String number;

  @MtField public int smsVerificationCode;
}
