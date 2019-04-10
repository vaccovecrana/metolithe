package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;
import io.vacco.metolithe.annotations.MtIdGroup;

@MtEntity(fixedId = false)
public class Bus {
  @MtId public int busId;

  @MtIdGroup(number = 0, position = 0)
  @MtAttribute(len = 8)
  public String licensePlate;

  @MtAttribute public int busNumber;
}
