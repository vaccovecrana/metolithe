package io.vacco.metolithe.codegen.dao;

import io.marioslab.basis.template.*;
import io.vacco.metolithe.core.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Function;

public class MtDaoMapper {

  private final Function<String, String> toBeanCase = (in) -> in.substring(0, 1).toUpperCase() + in.substring(1);

  public String mapFrom(MtDescriptor<?> d, String outPackage) {
    var loader = new TemplateLoader.ClasspathTemplateLoader();
    var template = loader.load("/io/vacco/metolithe/codegen/dao/MtDao.bt");
    var context = new TemplateContext();

    var mtPkClass = MtUtil.toWrapperClass(d.getPkField().isPresent() ? d.getPkField().get().getType() : Void.class);
    context.set("mtPackage", outPackage);
    context.set("mtPkClassName", mtPkClass.getCanonicalName());
    context.set("mtDaoClass", d.getType().getSimpleName());
    context.set("mtDsc", d);
    context.set("mtFields", d.getFields(true));
    context.set("toBeanCase", toBeanCase);
    context.set("toWrapper", (Function<Class<?>, String>) clz -> MtUtil.toWrapperClass(clz).getCanonicalName());

    return template.render(context);
  }

  public void mapSchema(File outDir, String outPackage, MtCaseFormat caseFormat, Class<?>... schemaClasses) {
    try {
      var out = new File(outDir, outPackage.replace(".", "/"));
      if (!out.exists() && !out.mkdirs()) {
        throw new IllegalStateException(out.getAbsolutePath());
      }
      for (Class<?> cl : schemaClasses) {
        var d = new MtDescriptor<>(cl, caseFormat);
        var daoSrc = mapFrom(d, outPackage);
        var daoFile = new File(out, String.format("%sDao.java", d.getType().getSimpleName()));
        Files.write(daoFile.toPath(), daoSrc.getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception e) {
      throw new MtException.MtDaoMappingException(outDir, outPackage, schemaClasses, e);
    }
  }
}
