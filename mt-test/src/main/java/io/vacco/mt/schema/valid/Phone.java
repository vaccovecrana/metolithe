package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.*;

@MtEntity()
public class Phone {

  @MtId private long phoneId;

  @MtId(groupTarget = false)
  @MtAttribute(nil = false)
  private long userId;

  @MtAttribute(nil = false, len = 16)
  @MtIdGroup(number = 0, position = 0)
  private String serialNumber;

  @MtIndex @MtAttribute(len = 12)
  private String number;

  @MtAttribute(nil = false)
  private boolean active;

  public String getSerialNumber() { return serialNumber; }
  public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

  public String getNumber() { return number; }
  public void setNumber(String number) { this.number = number; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public long getPhoneId() { return phoneId; }
  public long getUserId() { return userId; }
  public void setUserId(long userId) { this.userId = userId; }
}
