package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.codegen.liquibase.type.*;
import org.joox.Match;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

import static io.vacco.metolithe.codegen.liquibase.MtLb.*;
import static java.util.Objects.requireNonNull;
import static org.joox.JOOX.$;

public class MtLbXml {

  public Match mapSchema(Root r) {
    try {
      var xmlTemplate = MtLbXml.class.getClassLoader().getResource("io/vacco/metolithe/codegen/liquibase/changelog-template.xml");
      var lb = $(Objects.requireNonNull(xmlTemplate));
      for (var cs : r.databaseChangeLog) {
        Match m = map(cs,
          mt -> $(mt.getLabelName()),
          (mt, m0, m1) -> m0.append(m1),
          Match::attr,
          (mt, f, m0, m1) -> m0.append(m1)
        );
        lb.append(m);
      }
      return lb;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void writeSchema(Root r, OutputStream out) {
    try {
      requireNonNull(r);
      requireNonNull(out);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.transform(new DOMSource(mapSchema(r).document()), new StreamResult(out));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

}
