package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;

@MtEntity(fixedId = false)
public class Bus {
  @MtId public long busId;
  @MtId(position = 1) public String licensePlate;
  @MtAttribute public int busNumber;
}
