package io.vacco.mt.unit;

import io.vacco.metolithe.codegen.liquibase.*;
import io.vacco.metolithe.core.EntityDescriptor;
import io.vacco.metolithe.core.Murmur3LongGenerator;
import io.vacco.metolithe.extraction.EntityMetadata;
import io.vacco.metolithe.extraction.FieldMetadata;
import io.vacco.metolithe.extraction.TypeMapper;
import io.vacco.metolithe.util.Base64CollectionCodec;
import io.vacco.metolithe.util.Murmur3;
import io.vacco.metolithe.util.TypeUtil;
import io.vacco.mt.dao.*;
import io.vacco.mt.schema.invalid.*;
import io.vacco.mt.schema.valid.*;
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
import java.sql.DriverManager;
import java.util.*;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class MetoLitheSpec {

  private static Logger log = LoggerFactory.getLogger(MetoLitheSpec.class);

  private static FluentJdbc jdbc;
  private static SmartPhoneDao smartPhoneDao;
  private static PhoneDao phoneDao;
  private static Collection<EntityMetadata> entities;
  private static Map<Class<?>, Match> entityXmlNodes = new HashMap<>();
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

  private static long userId = 123456;

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
        () -> new EntityDescriptor<>(MissingIdEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE, null));
    it("Cannot describe an entity with duplicate id group positions.",
        c -> c.expected(IllegalArgumentException.class),
        () -> new EntityDescriptor<>(DuplicatePositionIdGroup.class, EntityDescriptor.CaseFormat.KEEP_CASE, null));
    it("Cannot describe a collection entity with attribute descriptions in collection fields.",
        c -> c.expected(IllegalStateException.class),
        () -> new EntityDescriptor<>(InvalidAttributeCollectionEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE, new Base64CollectionCodec()));
    it("Cannot describe a collection entity with index descriptions in collection fields.",
        c -> c.expected(IllegalStateException.class),
        () -> new EntityDescriptor<>(InvalidIndexCollectionAttribute.class, EntityDescriptor.CaseFormat.KEEP_CASE, new Base64CollectionCodec()));
    it("Cannot describe a collection entity without a collection codec.",
        c -> c.expected(IllegalStateException.class),
        () -> new EntityDescriptor<>(CollectionEntity.class, EntityDescriptor.CaseFormat.KEEP_CASE, null));
    it("Cannot access a non-existing entity field name.",
        c -> c.expected(IllegalArgumentException.class),
        () -> new EntityDescriptor<>(SmartPhone.class, EntityDescriptor.CaseFormat.KEEP_CASE, null).getField("lolo"));
    it("Cannot extract an invalid field.",
        c -> c.expected(IllegalArgumentException.class),
        () -> new EntityDescriptor<>(SmartPhone.class, EntityDescriptor.CaseFormat.KEEP_CASE, null).extract(null, "lolo"));
    it("Can extract an object's values without its primary key attribute.", () ->
        new EntityDescriptor<>(SmartPhone.class, EntityDescriptor.CaseFormat.KEEP_CASE, null)
            .extractAll(new SmartPhone(), false));
    it("Scans the target classpath packages for annotated classes.", () ->
        entities = new EntityExtractor().apply("io.vacco.mt.schema.valid"));
    it("Generates XML mappings for each entity.", () ->
        entities.forEach(em -> entityXmlNodes.put(em.getTarget(), XmlMapper.mapEntity(em, new TypeMapper()))));
    it("Prints each entity generated XML data.", () ->
        entityXmlNodes.values().stream().map(XmlChangeSetWriter::getData).forEach(log::info));
    it("Maps and writes entity XML files.", () -> {
      File targetDir = new File("/tmp/testlogs");
      targetDir.mkdirs();
      entityXmlNodes.forEach((clazz, xml) -> {
        File xmlFile = XmlChangeSetWriter.write(clazz, xml, targetDir);
        assertNotNull(xmlFile);
        assertTrue(xmlFile.exists());
        xmlFiles.add(xmlFile);
      });
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
    it("Cannot interact with an initialized Dao with mismatching primary key definitions.",
        c -> c.expected(IllegalArgumentException.class), () -> new MismatchingIdDao(jdbc, "public"));
    it("Cannot create a Dao for an entity with a mismatching collection codec definition.",
        c -> c.expected(IllegalStateException.class), () -> new InvalidCollectionTypeDao(jdbc, "public"));
    it("Initializes a new Dao.", () -> {
      smartPhoneDao = new SmartPhoneDao(jdbc, "public");
      assertNotNull(smartPhoneDao);
      assertNotNull(smartPhoneDao.getDescriptor().getFormat());
    });
    it("Cannot load a non-existing object.", () -> {
      Optional<SmartPhone> lol = smartPhoneDao.load(999L);
      assertFalse(lol.isPresent());
    });
    it("Cannot persist an entity without primary key groups and no explicitly assigned external id field.",
        c -> c.expected(IllegalStateException.class), () -> {
      BlogTagDao bd = new BlogTagDao(jdbc, "public");
      BlogTag bt = new BlogTag();
      bd.setId(bt);
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
      Collection<SmartPhone> spc = smartPhoneDao.loadWhereEnEq(SmartPhone.fields.number, phoneNo);
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
      long dc = smartPhoneDao.deleteWhereEnEq(SmartPhone.fields.number, phoneNo2);
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
    it("Can save an object which specifies collection fields.", () -> {
      CollectionEntityDao cd = new CollectionEntityDao(jdbc, "public");
      CollectionEntity ce = new CollectionEntity();
      ce.entName = "Zoidberg";
      ce.options = new TreeSet<>();
      ce.options.add("Why");
      ce.options.add("not");
      ce.options.add("Zoidberg?");
      cd.setId(ce);
      cd.save(ce);
      CollectionEntity ce0 = cd.loadExisting(ce.entId);
      assertNotNull(ce0);
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
      int id0 = bd.idOf(b);
      b.licensePlate = "2ZZ999";
      int id1 = bd.idOf(b);
      b.licensePlate = "5AA777";
      int id2 = bd.idOf(b);
      assertNotEquals(id0, id1);
      assertEquals(id0, id2);
    });
    it("Preserves the Id of a fixed Id entity when one of its attributes changes, and the object has been assigned an Id.", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setBatteryType(SmartPhone.BatteryType.LITHIUM_ION);
      sp.setDeviceUid("12345");
      sp.setSerialNumber(serialNo2);
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
      p.setUserId(userId);
      p = phoneDao.save(p);
      assertTrue(p.getPhoneId() != 0);
      assertNotNull(p);
    });
    it("Can create a list query of target parameters", () -> {
      Map<String, Object> params = new HashMap<>();
      Collection<String> items = new ArrayList<>();
      items.add("one");
      items.add("two");
      items.add("three");
      String queryPart = phoneDao.toNamedParam(params, items, "number");
      assertEquals(":number0, :number1, :number2", queryPart);
    });
    it("Converts primitive type classes to wrapper types.", () -> {
      TypeUtil.toWrapperClass(int.class);
      TypeUtil.toWrapperClass(double.class);
      TypeUtil.toWrapperClass(char.class);
      TypeUtil.toWrapperClass(boolean.class);
      TypeUtil.toWrapperClass(float.class);
      TypeUtil.toWrapperClass(short.class);
      TypeUtil.toWrapperClass(byte.class);
      assertFalse(TypeUtil.allNonNull(null));
      assertFalse(TypeUtil.allNonNull(new Object []{}));
    });
    it("Can delete an existing object based on its id.", () -> {
      SmartPhone sp = new SmartPhone();
      sp.setSerialNumber(serialNo2);
      sp.setNumber(phoneNo2);
      sp.setDeviceUid(deviceUid2);
      sp.setOs(SmartPhone.Os.IOS);
      smartPhoneDao.merge(sp);
      assertNotNull(smartPhoneDao.getGenerator());
      long newId = sp.getSpId();
      long result = smartPhoneDao.deleteWhereIdEq(newId);
      assertEquals(1, result);
    });
    it("Cannot persist an entity with incomplete id group fields.",
        c -> c.expected(IllegalStateException.class), () -> {
      PersonContactDao pdc = new PersonContactDao(jdbc, "public");
      PersonContact pc = new PersonContact();
      pc.firstName = "Mark";
      pdc.setId(pc);
    });
    it("Clears remaining coverage classes.", () -> {
      new CollectionEntity();
      new DuplicateIdEntity();
      new MismatchingIdEntity();
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

      Optional<FieldMetadata> ofm = new EntityMetadata(SmartPhone.class).rawFields().findFirst();
      assertTrue(ofm.isPresent());
      assertNotNull(ofm.get().getRawAnnotations());
      assertEquals(ofm.get(), ofm.get());
    });
  }
}
