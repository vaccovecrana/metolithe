package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class DeviceLocation {

  private static final String idxName = "gloc";

  @MtFk(Device.class)
  @MtUnique(idx = 0, inPk = false) public long did;
  @MtFk(User.class) public int uid;

  // not the best composite index, but a good example anyway.
  @St16 @MtCompIndex(name = idxName, idx = 0) public String geoHash2;
  @St16 @MtCompIndex(name = idxName, idx = 1) public String geoHash4;
  @St16 @MtCompIndex(name = idxName, idx = 2) public String geoHash12;
  @St16 public String geoHash12Ip;

  @MtField public long logtimeUtcMs;
  @MtField public double fraudScore;
}
