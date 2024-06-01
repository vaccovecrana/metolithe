package io.vacco.metolithe.schema;

import io.vacco.metolithe.annotations.*;

@MtEntity public class DeviceLocation {

  private static final String idxName = "gloc";

  @MtPk @MtFk(Device.class)
  public long did;

  @MtFk(DbUser.class) public int uid;

  // not the best composite index, but a good example anyway.
  @St16 @MtIndex(name = idxName, idx = 0) public String geoHash2;
  @St16 @MtIndex(name = idxName, idx = 1) public String geoHash4;
  @St16 @MtIndex(name = idxName, idx = 2) public String geoHash12;
  @St16 public String geoHash12Ip;

  @MtField public long logTimeUtcMs;
  @MtField public double fraudScore;
  @MtField public float  fraudScoreDelta;
}
