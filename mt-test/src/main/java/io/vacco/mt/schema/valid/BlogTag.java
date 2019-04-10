package io.vacco.mt.schema.valid;

import io.vacco.metolithe.annotations.*;

@MtEntity public class BlogTag {
  @MtId public int blogId = 0;
  @MtAttribute public long categoryFkId = 0;
}
