package io.vacco.metolithe.test;

import io.vacco.metolithe.annotations.MtFk;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.graph.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import org.slf4j.*;

import java.util.*;
import java.util.stream.*;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtCodeGenSpec extends MtSpec {

  private static final Logger log = LoggerFactory.getLogger(MtAnnotationsSpec.class);

  static {
    describe("Graph dependency traversal", () -> {
      it("Can find strongly connected nodes (Kosaraju-Sharir)", () -> {
        MtVtx<Integer> v0 = new MtVtx<>("0", 0);
        MtVtx<Integer> v1 = new MtVtx<>("1", 1);
        MtVtx<Integer> v2 = new MtVtx<>("2", 2);
        MtVtx<Integer> v3 = new MtVtx<>("3", 3);
        MtVtx<Integer> v4 = new MtVtx<>("4", 4);
        MtVtx<Integer> v5 = new MtVtx<>("5", 5);
        MtVtx<Integer> v6 = new MtVtx<>("6", 6);
        MtVtx<Integer> v7 = new MtVtx<>("7", 7);
        MtVtx<Integer> v8 = new MtVtx<>("8", 8);
        MtVtx<Integer> v9 = new MtVtx<>("9", 9);
        MtVtx<Integer> v10 = new MtVtx<>("10", 10);
        MtVtx<Integer> v11 = new MtVtx<>("11", 11);
        MtVtx<Integer> v12 = new MtVtx<>("12", 12);

        MtGrph<Integer> g1 = new MtGrph<>();

        g1.addEdge(v0, v1).addEdge(v0, v5)
            .addEdge(v2, v0).addEdge(v2, v3)
            .addEdge(v3, v2).addEdge(v3, v5)
            .addEdge(v4, v2).addEdge(v4, v3)
            .addEdge(v5, v4)
            .addEdge(v6, v0).addEdge(v6, v4).addEdge(v6, v8).addEdge(v6, v9)
            .addEdge(v7, v6).addEdge(v7, v9)
            .addEdge(v8, v6)
            .addEdge(v9, v10).addEdge(v9, v11)
            .addEdge(v10, v12)
            .addEdge(v11, v12).addEdge(v11, v4)
            .addEdge(v12, v9);

        int[] level = new int[]{0};
        MtDfs.apply(
            g1.reverse(),
            v -> {
              String sep = IntStream.range(0, level[0]).mapToObj(i -> " ").collect(Collectors.joining(""));
              log.info("{}dfs({})", sep, v.id);
              level[0] = level[0] + 1;
            },
            v -> {
              String sep = IntStream.range(0, level[0]).mapToObj(i -> " ").collect(Collectors.joining(""));
              log.info("{}{} done", sep, v.id);
              level[0] = level[0] - 1;
            }
        );

        MtKos.apply(g1).forEach((key, value) -> log.info("{} => {}", key, value));
      });
    });

    describe("MT Schema code generation", () -> {

      it("Traverses a graph", () -> {
        List<MtVtx<MtDescriptor<?>>> descriptors = Arrays.stream(testSchema)
            .map(MtDescriptor::new)
            .map(fd -> new MtVtx<MtDescriptor<?>>(fd.getTarget().getSimpleName(), fd))
            .collect(Collectors.toList());

        MtGrph<MtDescriptor<?>> schema = new MtGrph<>();

        for (MtVtx<MtDescriptor<?>> vd : descriptors) {
          vd.data.getFields(true).stream()
              .map(fd -> fd.get(MtFk.class))
              .filter(Optional::isPresent).map(Optional::get)
              .forEach(fk -> descriptors.stream()
                  .filter(d -> d.data.getTarget() == fk.value())
                  .findFirst().ifPresent(v0 -> schema.addEdge(vd, v0))
              );
        }

        MtKos.apply(schema).forEach((key, value) -> log.info("{} => {}", key, value));
      });
    });
  }

}
