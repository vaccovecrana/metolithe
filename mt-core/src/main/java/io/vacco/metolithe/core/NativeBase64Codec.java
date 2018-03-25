package io.vacco.metolithe.core;

import io.vacco.metolithe.spi.MtCodec;
import java.io.*;
import javax.validation.constraints.NotNull;
import java.util.Base64;

import static java.util.Objects.*;

public class NativeBase64Codec implements MtCodec {

  private final String SIGMA_COL = "Σ:";

  @Override
  public boolean isEncoded(@NotNull String input) {
    return input.startsWith(SIGMA_COL);
  }

  @Override
  public <T> String encode(@NotNull T input) {
    try {
      requireNonNull(input);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(input);
      oos.close();
      String b64 = Base64.getEncoder().encodeToString(baos.toByteArray());
      return String.format("%s%s", SIGMA_COL, b64);
    } catch (Exception e) { throw new IllegalStateException(e); }
  }

  @Override
  public <T> T decode(String input) {
    try {
      requireNonNull(input);
      if (!isEncoded(input)) { throw new IllegalArgumentException("Invalid payload."); }
      String b64 = input.substring(2);
      byte [] data = Base64.getDecoder().decode(b64);
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object payload = ois.readObject();
      ois.close();
      return (T) payload;
    } catch (Exception e) { throw new IllegalStateException(e); }
  }
}
