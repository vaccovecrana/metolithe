package io.vacco.metolithe.test;

import io.vacco.metolithe.codegen.dao.MtDaoMapper;
import io.vacco.metolithe.codegen.liquibase.*;
import io.vacco.metolithe.schema.Phone;
import io.vacco.metolithe.schema.User;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import liquibase.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.*;
import org.joox.Match;
import org.junit.runner.RunWith;
import java.io.*;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtCodeGenSpec extends MtSpec {

  static {

    File xmlFile = new File("/tmp", "mt-test.xml");
    ds.setURL("jdbc:h2:mem:public;DB_CLOSE_DELAY=-1");

    describe("MT Schema code generation", () -> {
      it("Generates typed field DAO definitions", () -> {
        File out = new File(".", "src/main/java");
        new MtDaoMapper().mapSchema(out, "io.vacco.metolithe.test.dao", Phone.class, User.class);
      });
      it("Generates Liquibase changelogs", () -> {
        Match lbChangeLog = new MtLbMapper().mapSchema(fmt, testSchema);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MtLbWriter.writeTo(lbChangeLog, baos);
        MtLbWriter.writeTo(lbChangeLog, new FileOutputStream(xmlFile));
        log.info(new String(baos.toByteArray()));
      });
      it("Creates an in-memory database and applies the generated change logs.", () -> {
        JdbcConnection c = new JdbcConnection(ds.getConnection());
        ResourceAccessor ra = new FileSystemResourceAccessor(xmlFile.getParentFile().getAbsolutePath());
        Liquibase lb = new Liquibase(xmlFile.getAbsolutePath(), ra, c);
        lb.update(new Contexts(), new LabelExpression());
        assertNotNull(c);
        c.close();
      });
    });
  }

}
