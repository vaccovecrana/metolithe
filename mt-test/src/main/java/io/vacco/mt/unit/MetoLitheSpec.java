package io.vacco.mt.unit;

import io.vacco.metolithe.codegen.liquibase.*;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.metolithe.util.Murmur3;
import io.vacco.metolithe.util.TypeUtil;
import io.vacco.mt.dao.*;
import io.vacco.mt.schema.invalid.CollectionEntity;
import io.vacco.mt.schema.invalid.DuplicateIdEntity;
import io.vacco.mt.schema.invalid.InvalidEntity;
import io.vacco.mt.schema.valid.Bus;
import io.vacco.mt.schema.valid.Dummy;
import io.vacco.mt.schema.valid.Phone;
import io.vacco.mt.schema.valid.SmartPhone;
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
  private static String deviceUid1 = "ABCDEF-012345-BBBAAA";
  private static String deviceUid2 = "ZZZYYY-XXXXXX-999888";
  private static String serialNo1 = "ABC-DEF-GHI-JKLM";
  private static String serialNo2 = "123-456-789-0123";
  private static String serialNo3 = "AAA-BBB-CCC-DDD";
  private static String phoneNo = "555-555-5555";
  private static String phoneNo2 = "617-555-5555";
  private static String phoneNo3 = "787-555-5555";

  private static long generatedId1;
  private static long generatedId2;

  static {
    beforeAll(() -> {
      JdbcDataSource ds = new JdbcDataSource();
      ds.setURL(dbUrl);
      jdbc = new FluentJdbcBuilder().connectionProvider(ds).build();
    });
    it("Can apply Murmur3 hash on values using a custom seed.", () -> {
      long val = new Murmur3LongGenerator(12345).apply("Hello", 0, 999L, "cool");
      assertNotEquals(0, val);
    });
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
            .extractAll(new SmartPhone(), false)
    );
    it("Cannot map a class with invalid data.", c -> c.expected(IllegalStateException.class),
        () -> XmlMapper.mapEntity(SmartPhone.class, null)
    );
    it("Scans the target classpath packages for annotated classes.", () ->
        new EntityExtractor().apply(EntityDescriptor.CaseFormat.KEEP_CASE,"io.vacco.mt.schema.valid")
            .forEach(ed0 -> {
              entities.put(ed0.getTarget(), ed0.getAllFields());
              log.info(ed0.getFormat().toString());
            })
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
    it("Cannot create a Dao for an entity with duplicate primary key field positions.",
        c -> c.expected(IllegalStateException.class), () -> new DuplicateIdEntityDao(jdbc, "public"));
    it("Cannot create a Dao for an entity specifying collection fields.",
        c -> c.expected(IllegalArgumentException.class),
        () -> new CollectionEntityDao(jdbc, "public"));
    it("Cannot interact with an initialized Dao with mismatching primary key definitions.",
        c -> c.expected(IllegalArgumentException.class), () -> new InvalidEntityDao(jdbc, "public"));
    it("Initializes a new Dao.", () -> {
      smartPhoneDao = new SmartPhoneDao(jdbc, "public");
      assertNotNull(smartPhoneDao);
    });
    it("Cannot load a non-existing object.", () -> {
      Optional<SmartPhone> lol = smartPhoneDao.load(999L);
      assertFalse(lol.isPresent());
    });
    it("Fails to explicitly load an non-existing object.", c -> c.expected(IllegalArgumentException.class),
        () -> smartPhoneDao.loadExisting(99999L)
    );
    it("Can save an object.", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setBatteryType(SmartPhone.BatteryType.LITHIUM_ION);
      sp.setDeviceUid(deviceUid1);
      sp.setGpsPrecision(0.8);
      sp.setOs(SmartPhone.Os.ANDROID);
      sp.setActive(true);
      sp.setNumber(phoneNo);
      sp.setSerialNumber(serialNo1);
      sp = smartPhoneDao.save(sp);
      assertNotNull(sp);
      generatedId1 = sp.getSpId();
    });
    it("Can load an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(generatedId1);
      assertNotNull(sp);
      Optional<SmartPhone> osp = smartPhoneDao.load(generatedId1);
      assertTrue(osp.isPresent());
    });
    it("Can find an object by an attribute.", () -> {
      Collection<SmartPhone> spc = smartPhoneDao.loadWhereEq("number", phoneNo);
      assertNotNull(spc);
      assertEquals(spc.size(), 1);
      assertEquals(phoneNo, spc.iterator().next().getNumber());
    });
    it("Can update an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(generatedId1);
      sp.setBatteryType(null);
      smartPhoneDao.update(sp);
    });
    it("Can merge changes to an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(generatedId1);
      sp.setBatteryType(SmartPhone.BatteryType.GRAPHENE);
      sp = smartPhoneDao.merge(sp);
      assertEquals(SmartPhone.BatteryType.GRAPHENE, sp.getBatteryType());
    });
    it("Can merge a new object", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setSerialNumber(serialNo2);
      sp.setNumber(phoneNo2);
      sp.setDeviceUid(deviceUid2);
      sp.setOs(SmartPhone.Os.IOS);
      smartPhoneDao.merge(sp);
      generatedId2 = sp.getSpId();
    });
    it("Can delete an existing object.", () -> {
      SmartPhone sp = smartPhoneDao.loadExisting(generatedId2);
      long dc = smartPhoneDao.delete(sp);
      assertEquals(1, dc);
      smartPhoneDao.merge(sp);
    });
    it("Can delete an existing object based on an attribute value.", () -> {
      long dc = smartPhoneDao.deleteWhereEq("number", phoneNo2);
      assertEquals(1, dc);
      SmartPhone sp = new SmartPhone();
      sp.setSerialNumber(serialNo2);
      sp.setNumber(phoneNo2);
      sp.setDeviceUid(deviceUid2);
      sp.setOs(SmartPhone.Os.IOS);
      smartPhoneDao.merge(sp);
    });
    it("Can update multiple objects within a transaction.", () -> {
      SmartPhone sp0 = smartPhoneDao.loadExisting(generatedId1);
      SmartPhone sp1 = smartPhoneDao.loadExisting(generatedId2);
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
      SmartPhone sp0 = smartPhoneDao.loadExisting(generatedId1);
      SmartPhone sp1 = smartPhoneDao.loadExisting(generatedId2);
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
    it("Changes the Id of a non-fixed Id entity when one of its attributes changes.", () -> {
      BusDao bd = new BusDao(jdbc, "public");
      Bus b = new Bus();
      b.licensePlate = "5AA777";
      long id0 = bd.idOf(b);
      b.licensePlate = "2ZZ999";
      long id1 = bd.idOf(b);
      b.licensePlate = "5AA777";
      long id2 = bd.idOf(b);
      assertNotEquals(id0, id1);
      assertEquals(id0, id2);
    });
    it("Preserves the Id of a fixed Id entity when one of its attributes changes and the object has been assigned an Id.", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setBatteryType(SmartPhone.BatteryType.LITHIUM_ION);
      sp.setDeviceUid("12345");
      sp.setGpsPrecision(1.0);
      sp.setOs(SmartPhone.Os.ANDROID);
      smartPhoneDao.setId(sp);
      long id0 = smartPhoneDao.idOf(sp);
      sp.setDeviceUid("ABCDE");
      long id1 = smartPhoneDao.idOf(sp);
      assertEquals(id0, id1);
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
      p.setNumber(phoneNo3);
      p.setSerialNumber(serialNo3);
      p = phoneDao.save(p);
      assertNotNull(p);
    });
    it("Converts primitive type classes to wrapper types.", () -> {
      TypeUtil.toWrapperClass(int.class);
      TypeUtil.toWrapperClass(double.class);
      TypeUtil.toWrapperClass(char.class);
      TypeUtil.toWrapperClass(boolean.class);
      TypeUtil.toWrapperClass(float.class);
      TypeUtil.toWrapperClass(short.class);
      TypeUtil.toWrapperClass(byte.class);
    });
    it("Can delete an existing object based on its id.", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setSerialNumber(serialNo2);
      sp.setNumber(phoneNo2);
      sp.setDeviceUid(deviceUid2);
      sp.setOs(SmartPhone.Os.IOS);
      smartPhoneDao.merge(sp);
      long newId = sp.getSpId();
      long result = smartPhoneDao.deleteWhereIdEq(newId);
      assertEquals(1, result);
    });
    it("Clears remaining coverage classes.", () -> {
      new CollectionEntity();
      new DuplicateIdEntity();
      new InvalidEntity();
      new Dummy();
      new Bus();
      new TypeUtil();
      new Murmur3();

      Phone p = new Phone();
      p.getSerialNumber();
      p.isActive();
      p.getPhoneId();

      SmartPhone sp = new SmartPhone();
      sp.getDeviceUid();
      sp.getOs();
      sp.getGpsPrecision();
    });
  }
}
