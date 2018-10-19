package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.MtAttribute;

public class Dummy {
  @MtAttribute
  private long serialNumber;

  @MtAttribute(len = 12)
  private String number;
}
