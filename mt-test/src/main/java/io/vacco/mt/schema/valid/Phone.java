package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;
import io.vacco.metolithe.annotations.MtIndex;

@MtEntity()
public class Phone {

  @MtId private long phoneId;

  @MtAttribute(len = 16) @MtId(position = 1)
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
}
