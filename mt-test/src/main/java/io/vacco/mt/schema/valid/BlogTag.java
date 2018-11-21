package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.MtAttribute;
import io.vacco.metolithe.annotations.MtEntity;
import io.vacco.metolithe.annotations.MtId;

@MtEntity
public class BlogTag {
  @MtId public long blogId = 0;
  @MtAttribute public long categoryFkId = 0;
}
