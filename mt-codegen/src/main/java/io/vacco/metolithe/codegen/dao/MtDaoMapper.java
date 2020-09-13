package io.vacco.metolithe.codegen.dao;

import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtTypeMapper;

import java.util.function.Function;

public class MtDaoMapper {

  private final Function<String, String> toBeanCase = (in) -> in.substring(0, 1).toUpperCase() + in.substring(1);

  public String mapFrom(MtDescriptor<?> d, String outPackage) {
    TemplateLoader loader = new TemplateLoader.ClasspathTemplateLoader();
    Template template = loader.load("/io/vacco/metolithe/codegen/dao/MtDao.hbs");
    TemplateContext context = new TemplateContext();

    Class<?> mtPkClass = MtTypeMapper.toWrapperClass(d.getPkField().isPresent() ? d.getPkField().get().getType() : Void.class);
    context.set("mtPackage", outPackage);
    context.set("mtPkClassName", mtPkClass.getCanonicalName());
    context.set("mtDsc", d);
    context.set("mtFields", d.getFields(false));
    context.set("toBeanCase", toBeanCase);
    context.set("toWrapper", (Function<Class<?>, String>) MtTypeMapper::wrapperClassOf);

    return template.render(context);
  }
}
