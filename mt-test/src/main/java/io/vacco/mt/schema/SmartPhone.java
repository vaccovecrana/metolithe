package io.vacco.mt.schema;

import io.vacco.metolithe.annotations.*;
import java.util.Set;

@MtEntity
public class SmartPhone extends Phone {

  public enum Os { IOS, ANDROID }
  public enum BatteryType { LITHIUM_ION, GRAPHENE }

  @MtId @MtAttribute(len = 128)
  private String deviceUid;

  @MtIndex @MtAttribute(nil = false, len = 16)
  private Os os;

  @MtIndex @MtAttribute(len = 32)
  private BatteryType batteryType;

  @MtAttribute()
  private double gpsPrecision;

  public String getDeviceUid() {
    return deviceUid;
  }
  public void setDeviceUid(String deviceUid) {
    this.deviceUid = deviceUid;
  }

  public Os getOs() {
    return os;
  }
  public void setOs(Os os) {
    this.os = os;
  }

  public BatteryType getBatteryType() {
    return batteryType;
  }
  public void setBatteryType(BatteryType batteryType) {
    this.batteryType = batteryType;
  }

  public double getGpsPrecision() {
    return gpsPrecision;
  }
  public void setGpsPrecision(double gpsPrecision) {
    this.gpsPrecision = gpsPrecision;
  }

}
