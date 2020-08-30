package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class DeviceLocation {

  private static final String idxName = "geo_idx";

  @MtFk(Device.class) public long did;
  @MtFk(User.class) public int uid;

  // not the best composite index, but a good example anyway.
  @St16 @MtCompIndex(name = idxName, position = 0) public String geoHash2;
  @St16 @MtCompIndex(name = idxName, position = 1) public String geoHash4;
  @St16 @MtCompIndex(name = idxName, position = 2) public String geoHash12;
  @St16 public String geoHash12Ip;

  @MtField public long logtimeUtcMs;
  @MtField public double fraudScore;
}
