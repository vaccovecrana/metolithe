package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.*;

@MtEntity
public class PersonContact {

  @MtId public long pid;

  @MtIdGroup(number = 2, position = 0)
  @MtAttribute(nil = false, len = 16) public String firstName;
  @MtIdGroup(number = 2, position = 1)
  @MtAttribute(nil = false, len = 16) public String lastName;

  @MtIdGroup(number = 0, position = 0)
  @MtAttribute(len = 16) public String email;

  @MtIdGroup(number = 1, position = 0)
  @MtAttribute(len = 16) public String phoneNo;
}
