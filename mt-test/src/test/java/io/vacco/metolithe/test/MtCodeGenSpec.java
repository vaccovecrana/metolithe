package io.vacco.metolithe.test;

import io.vacco.metolithe.codegen.liquibase.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import liquibase.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.*;
import org.joox.Match;
import org.junit.runner.RunWith;
import org.slf4j.*;
import java.io.*;
import java.sql.DriverManager;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtCodeGenSpec extends MtSpec {

  private static final Logger log = LoggerFactory.getLogger(MtAnnotationsSpec.class);

  static {

    File xmlFile = new File("/tmp", "mt-test.xml");
    String dbUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    describe("MT Schema code generation", () -> {
      it("Generates Liquibase changelogs", () -> {
        Match lbChangeLog = new MtLbMapper().mapSchema(testSchema);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MtLbWriter.writeTo(lbChangeLog, baos);
        MtLbWriter.writeTo(lbChangeLog, new FileOutputStream(xmlFile));
        log.info(new String(baos.toByteArray()));
      });
      it("Creates an in-memory database and applies the generated change logs.", () -> {
        JdbcConnection conn = new JdbcConnection(DriverManager.getConnection(dbUrl));
        try {
          ResourceAccessor ra = new FileSystemResourceAccessor(xmlFile.getParentFile().getAbsolutePath());
          Liquibase lb = new Liquibase(xmlFile.getAbsolutePath(), ra, conn);
          lb.update(new Contexts(), new LabelExpression());
        } catch (Exception e) { throw new IllegalStateException(e); }
        assertNotNull(conn);
        conn.close();
      });
    });
  }

}
