package io.vacco.metolithe.codegen.liquibase;

import org.joox.Match;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static java.util.Objects.*;

public class XmlChangeSetWriter {

  public static void writeTo(Match node, OutputStream out) {
    try {
      requireNonNull(node);
      requireNonNull(out);
      requireNonNull(node);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.transform(new DOMSource(node.document()), new StreamResult(out));
    } catch (Exception e) { throw new IllegalStateException(e); }
  }

  public static String getData(Match rootNode) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    writeTo(rootNode, baos);
    return baos.toString();
  }

  public static File write(Class<?> entity, Match xmlData, File outDir) {
    try {
      requireNonNull(entity);
      requireNonNull(xmlData);
      requireNonNull(outDir);
      if (!outDir.exists() || !outDir.isDirectory()) {
        throw new IllegalArgumentException("Bad output folder " + outDir.getAbsolutePath());
      }
      File xmlFile = new File(outDir, String.format("%s.xml", toDashCase(entity.getSimpleName())));
      writeTo(xmlData, new FileOutputStream(xmlFile));
      return xmlFile;
    } catch (Exception e) { throw new IllegalStateException(e); }
  }

  private static String toDashCase(String in) {
    return requireNonNull(in).replaceAll("(.)(\\p{Upper})", "$1-$2").toLowerCase();
  }
}
