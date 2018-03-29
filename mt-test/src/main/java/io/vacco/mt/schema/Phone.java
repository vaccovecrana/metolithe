package io.vacco.mt.schema;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;

@MtEntity
public abstract class Phone {

  @MtId @MtAttribute
  private long serialNumber;

  @MtAttribute(len = 12)
  private String number;

  @MtAttribute(nil = false)
  private boolean active;

  public long getSerialNumber() { return serialNumber; }
  public void setSerialNumber(long serialNumber) { this.serialNumber = serialNumber; }

  public String getNumber() { return number; }
  public void setNumber(String number) { this.number = number; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
}
