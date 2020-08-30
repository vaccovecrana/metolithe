package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class Phone {

  @MtPk public int pid;

  @MtField @MtNotNull @MtUnique(position = 0) @MtIndex
  public int countryCode;

  @St16 @MtUnique(position = 1)
  public String number;

  @MtField public int smsVerificationCode;
}
