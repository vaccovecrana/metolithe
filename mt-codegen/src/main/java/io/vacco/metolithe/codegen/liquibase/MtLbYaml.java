package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.codegen.liquibase.type.Root;
import org.yaml.snakeyaml.Yaml;
import java.io.Writer;
import java.util.*;

import static io.vacco.metolithe.codegen.liquibase.MtLb.map;

public class MtLbYaml {

  private final Yaml y = new Yaml();

  public Map<String, Object> mapSchema(Root r) {
    return map(r,
      mt -> new LinkedHashMap<>(),
      (mt, m0, m1) -> m0.put(mt.getLabelName(), m1),
      HashMap::put,
      (mt, f, m0, m1) -> {
        @SuppressWarnings("unchecked")
        var l0 = (List<Object>) m0.computeIfAbsent(f.getName(), k -> new ArrayList<>());
        var tagMap = new LinkedHashMap<String, Object>();
        tagMap.put(mt.getLabelName(), m1);
        l0.add(tagMap);
      }
    );
  }

  public void writeSchema(Root r, Writer w) {
    y.dump(mapSchema(r), w);
  }
}
