package io.vacco.metolithe.codegen.dao;

import io.marioslab.basis.template.*;
import io.vacco.metolithe.core.*;
import io.vacco.oruzka.core.OzReflect;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Function;

public class MtDaoMapper {

  private final Function<String, String> toBeanCase = (in) -> in.substring(0, 1).toUpperCase() + in.substring(1);

  public String mapFrom(MtDescriptor<?> d, String outPackage) {
    TemplateLoader loader = new TemplateLoader.ClasspathTemplateLoader();
    Template template = loader.load("/io/vacco/metolithe/codegen/dao/MtDao.bt");
    TemplateContext context = new TemplateContext();

    Class<?> mtPkClass = OzReflect.toWrapperClass(d.getPkField().isPresent() ? d.getPkField().get().getType() : Void.class);
    context.set("mtPackage", outPackage);
    context.set("mtPkClassName", mtPkClass.getCanonicalName());
    context.set("mtDsc", d);
    context.set("mtFields", d.getFields(false));
    context.set("toBeanCase", toBeanCase);
    context.set("toWrapper", (Function<Class<?>, String>) clz -> OzReflect.toWrapperClass(clz).getCanonicalName());

    return template.render(context);
  }

  public void mapSchema(File outDir, String outPackage, Class<?> ... schemaClasses) {
    try {
      File out = new File(outDir, outPackage.replace(".", "/"));
      if (!out.exists() && !out.mkdirs()) { throw new IllegalStateException(out.getAbsolutePath()); }
      for (Class<?> cl : schemaClasses) {
        MtDescriptor<?> d = new MtDescriptor<>(cl, MtCaseFormat.KEEP_CASE);
        String daoSrc = mapFrom(d, outPackage);
        File daoFile = new File(out, String.format("%sDao.java", d.getName()));
        Files.write(daoFile.toPath(), daoSrc.getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception e) {
      throw new MtException.MtDaoMappingException(outDir, outPackage, schemaClasses, e);
    }
  }
}
