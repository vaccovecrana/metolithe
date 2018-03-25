package io.vacco.mt.schema;

import io.vacco.metolithe.annotations.MtAttribute;
import javax.validation.constraints.*;

public abstract class Phone {

  @MtAttribute
  @DecimalMin("8") @DecimalMax("32")
  private long serialNumber;

  @MtAttribute
  @Size(min = 7, max = 12)
  private String number;

  @MtAttribute
  @NotNull
  private boolean active;

  public long getSerialNumber() { return serialNumber; }
  public void setSerialNumber(long serialNumber) { this.serialNumber = serialNumber; }

  public String getNumber() { return number; }
  public void setNumber(String number) { this.number = number; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
}
