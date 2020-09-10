package io.vacco.metolithe.test;

import io.vacco.metolithe.codegen.liquibase.*;
import io.vacco.metolithe.core.MtCaseFormat;
import io.vacco.metolithe.core.MtDescriptor;
import io.vacco.metolithe.core.MtMurmur3IFn;
import io.vacco.metolithe.core.MtWriteDao;
import io.vacco.metolithe.schema.Phone;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import liquibase.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.*;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.joox.Match;
import org.junit.runner.RunWith;
import org.slf4j.*;
import java.io.*;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;
import static io.vacco.shax.logging.ShArgument.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MtCodeGenSpec extends MtSpec {

  private static final Logger log = LoggerFactory.getLogger(MtAnnotationsSpec.class);

  private static final JdbcDataSource ds = new JdbcDataSource();

  static {

    MtCaseFormat fmt = MtCaseFormat.UPPER_CASE;
    File xmlFile = new File("/tmp", "mt-test.xml");
    ds.setURL("jdbc:h2:mem:public;DB_CLOSE_DELAY=-1");

    describe("MT Schema code generation", () -> {
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
      it("Creates base DAOs for inserting data", () -> {
        MtWriteDao<Phone, Integer> pDao = new MtWriteDao<>("public",
            new FluentJdbcBuilder().connectionProvider(ds).build(),
            new MtDescriptor<>(Phone.class, fmt), new MtMurmur3IFn()
        );

        log.info("{}", kv("p0", pDao.save(p0)));

        Phone p1 = pDao.loadExisting(p0.pid);
        assertEquals(p0.pid, p1.pid);
        assertEquals(p0.countryCode, p1.countryCode);
        assertEquals(p0.number, p1.number);
      });
    });
  }

}
