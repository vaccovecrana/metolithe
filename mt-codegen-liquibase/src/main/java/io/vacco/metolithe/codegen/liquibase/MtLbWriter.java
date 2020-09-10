package io.vacco.metolithe.codegen.liquibase;

import org.joox.Match;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static java.util.Objects.*;

public class MtLbWriter {

  public static void writeTo(Match node, OutputStream out) throws TransformerException {
    requireNonNull(node);
    requireNonNull(out);
    requireNonNull(node);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(new DOMSource(node.document()), new StreamResult(out));
  }
}
