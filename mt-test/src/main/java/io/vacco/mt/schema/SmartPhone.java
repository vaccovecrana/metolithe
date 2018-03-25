package io.vacco.mt.schema;

import io.vacco.metolithe.annotations.*;
import javax.validation.constraints.*;
import java.util.Set;

@MtEntity
public class SmartPhone extends Phone {

  public enum Os { IOS, ANDROID }
  public enum Feature { FORCE_TOUCH, FACE_DETECTION, BEZELLESS_DISPLAY, WIRELESS_CHARGNING }
  public enum BatteryType { LITHIUM_ION, GRAPHENE }

  @MtId
  @Size(max = 128)
  private String deviceUid;

  @MtIndex
  @MtAttribute(maxByteLength = 16)
  @NotNull private Os os;

  @MtIndex
  @MtAttribute(maxByteLength = 32)
  private BatteryType batteryType;

  @MtAttribute
  @DecimalMin("0.0")
  @DecimalMax("1.0")
  private double gpsPrecision;

  @MtAttribute(maxByteLength = 512)
  private Set<Feature> features;

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

  public Set<Feature> getFeatures() {
    return features;
  }
  public void setFeatures(Set<Feature> features) {
    this.features = features;
  }
}
