package io.vacco.mt.unit;

import io.vacco.metolithe.codegen.liquibase.*;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.mt.dao.PhoneDao;
import io.vacco.mt.dao.SmartPhoneDao;
import io.vacco.mt.schema.Dummy;
import io.vacco.mt.schema.Phone;
import io.vacco.mt.schema.SmartPhone;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.h2.jdbcx.JdbcDataSource;
import org.joox.Match;
import org.junit.runner.RunWith;
import org.slf4j.*;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MetoLitheSpec {

  private static Logger log = LoggerFactory.getLogger(MetoLitheSpec.class);

  private static FluentJdbc jdbc;
  private static SmartPhoneDao smartPhoneDao;
  private static PhoneDao phoneDao;
  private static Map<Class<?>, Collection<Field>> entities = new HashMap<>();
  private static List<Match> entityXmlNodes = new ArrayList<>();
  private static List<File> xmlFiles = new ArrayList<>();

  private static String dbUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static String phoneId = "123456";
  private static String phoneId2 = "ABCDEF";
  private static String phoneNo = "555-555-5555";
  private static String phoneNo2 = "617-555-5555";

  static {
    it("Cannot describe an entity without a primary key attribute.",
        c -> c.expected(IllegalStateException.class),
        () -> new EntityDescriptor<>(Dummy.class, EntityDescriptor.CaseFormat.KEEP_CASE));
    it("Cannot access a non-existing entity field name.",
        c -> c.expected(IllegalArgumentException.class),
        () -> new EntityDescriptor<>(SmartPhone.class, EntityDescriptor.CaseFormat.KEEP_CASE).getField("lolo"));
    it("Cannot extract an invalid field.",
        c -> c.expected(IllegalStateException.class),
        () -> new EntityDescriptor<>(SmartPhone.class, EntityDescriptor.CaseFormat.KEEP_CASE).extract(null, "lolo"));
    it("Can extract an object's values without its primary key attribute.", () ->
        new EntityDescriptor<>(SmartPhone.class, EntityDescriptor.CaseFormat.KEEP_CASE)
            .extractAll(new SmartPhone(), Function.identity(), false)
    );
    it("Cannot map a class with invalid data.", c -> c.expected(IllegalStateException.class),
        () -> XmlMapper.mapEntity(SmartPhone.class, null)
    );
    it("Scans the target classpath packages for annotated classes.", () ->
        new EntityExtractor().apply(EntityDescriptor.CaseFormat.KEEP_CASE,"io.vacco.mt.schema").forEach(ed0 ->
            entities.put(ed0.getTarget(), ed0.getAllFields()))
    );
    it("Generates xml mappings for extracted entities.", () ->
        entityXmlNodes.addAll(
            entities.entrySet().stream().map(e0 ->
                XmlMapper.mapEntity(e0.getKey(), e0.getValue())
            ).collect(Collectors.toList()))
    );
    it("Prints each entity generated XML data.", () ->
        entityXmlNodes.stream().map(XmlChangeSetWriter::getData).forEach(log::info));
    it("Maps and writes entity XML files.", () -> {
      File targetDir = new File("/tmp/testlogs");
      targetDir.mkdirs();
      for (Map.Entry<Class<?>, Collection<Field>> e0 : entities.entrySet()) {
        Match xml = XmlMapper.mapEntity(e0.getKey(), e0.getValue());
        File xmlFile = XmlChangeSetWriter.write(e0.getKey(), xml, targetDir);
        assertNotNull(xmlFile);
        assertTrue(xmlFile.exists());
        xmlFiles.add(xmlFile);
      }
    });
    it("Creates an in-memory database and applies the generated change logs.", () -> {
      JdbcConnection conn = new JdbcConnection(DriverManager.getConnection(dbUrl));
      xmlFiles.forEach(xmlf0 -> {
        try {
          Liquibase lb = new Liquibase(xmlf0.getAbsolutePath(),
              new FileSystemResourceAccessor(xmlf0.getParentFile().getAbsolutePath()), conn);
          lb.update(new Contexts(), new LabelExpression());
        } catch (Exception e) { throw new IllegalStateException(e); }
      });
      assertNotNull(conn);
      conn.close();
    });

    it("Initializes a new Dao.", () -> {
      JdbcDataSource ds = new JdbcDataSource();
      ds.setURL(dbUrl);
      jdbc = new FluentJdbcBuilder().connectionProvider(ds).build();
      smartPhoneDao = new SmartPhoneDao(jdbc, "public");
      assertNotNull(smartPhoneDao);
    });
    it("Cannot load a non-existing object.", () -> {
      Optional<SmartPhone> lol = smartPhoneDao.load("lol");
      assertFalse(lol.isPresent());
    });
    it("Fails to explicitly load an non-existing object.", c -> c.expected(IllegalArgumentException.class),
        () -> smartPhoneDao.loadExisting("lol")
    );
    it("Can save an object.", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setBatteryType(SmartPhone.BatteryType.LITHIUM_ION);
      sp.setDeviceUid(phoneId);
      sp.setGpsPrecision(0.8);
      sp.setOs(SmartPhone.Os.ANDROID);
      sp.setActive(true);
      sp.setNumber(phoneNo);
      sp.setSerialNumber("123456");
      sp = smartPhoneDao.save(sp);
      assertNotNull(sp);
    });
    it("Can load an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(phoneId);
      assertNotNull(sp);
      Optional<SmartPhone> osp = smartPhoneDao.load(phoneId);
      assertTrue(osp.isPresent());
    });
    it("Can find an object by an attribute.", () -> {
      Collection<SmartPhone> spc = smartPhoneDao.loadWhereEq("number", phoneNo);
      assertNotNull(spc);
      assertEquals(spc.size(), 1);
      assertEquals(phoneNo, spc.iterator().next().getNumber());
    });
    it("Can update an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(phoneId);
      sp.setBatteryType(null);
      smartPhoneDao.update(sp);
    });
    it("Can merge changes to an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(phoneId);
      sp.setBatteryType(SmartPhone.BatteryType.GRAPHENE);
      sp = smartPhoneDao.merge(sp);
      assertEquals(SmartPhone.BatteryType.GRAPHENE, sp.getBatteryType());
    });
    it("Can merge a new object", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setSerialNumber("567890");
      sp.setNumber(phoneNo2);
      sp.setDeviceUid(phoneId2);
      sp.setOs(SmartPhone.Os.IOS);
      smartPhoneDao.merge(sp);
    });
    it("Can delete an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(phoneId2);
      long dc = smartPhoneDao.delete(sp);
      assertEquals(1, dc);
      smartPhoneDao.merge(sp);
    });
    it("Can delete an existing object based on an attribute value.", () -> {
      long dc = smartPhoneDao.deleteWhereEq("number", phoneNo2);
      assertEquals(1, dc);
      SmartPhone sp = new SmartPhone();
      sp.setSerialNumber("567890");
      sp.setNumber(phoneNo2);
      sp.setDeviceUid(phoneId2);
      sp.setOs(SmartPhone.Os.IOS);
      smartPhoneDao.merge(sp);
    });

    it("Can update multiple objects within a transaction.", () -> {
      SmartPhone sp0 = smartPhoneDao.loadExisting(phoneId);
      SmartPhone sp1 = smartPhoneDao.loadExisting(phoneId2);
      boolean b = smartPhoneDao.inTransaction(() -> {
        sp0.setActive(false);
        sp1.setActive(false);
        smartPhoneDao.merge(sp0);
        smartPhoneDao.merge(sp1);
        return true;
      });
      assertTrue(b);
    });
    it("Can rollback a transaction.", c -> c.expected(IllegalStateException.class), () -> {
      SmartPhone sp0 = smartPhoneDao.loadExisting(phoneId);
      SmartPhone sp1 = smartPhoneDao.loadExisting(phoneId2);
      smartPhoneDao.inTransaction(() -> {
        sp0.setDeviceUid(null);
        sp0.setActive(false);
        sp1.setActive(false);
        smartPhoneDao.merge(sp0);
        smartPhoneDao.merge(sp1);
        return true;
      });
    });
    it("Can retrieve a mapper for a registered class.",  () -> {
      Mapper<SmartPhone> mapper = smartPhoneDao.mapperFor(SmartPhone.class);
      assertNotNull(mapper);
    });

    it("Initializes an upper case dao.", () -> {
      JdbcDataSource ds = new JdbcDataSource();
      ds.setURL(dbUrl);
      jdbc = new FluentJdbcBuilder().connectionProvider(ds).build();
      phoneDao = new PhoneDao(jdbc, "public");
      assertNotNull(phoneDao);
    });
    it("Can save objects in upper case.", () -> {
      Phone p = new Phone();
      p.setActive(true);
      p.setNumber("6175555555");
      p.setSerialNumber("ANDROIDLOLOLOL");
      p = phoneDao.save(p);
      assertNotNull(p);
    });
  }
}
