package io.vacco.mt.unit

import io.vacco.metolithe.core.NativeBase64Codec
import io.vacco.mt.dao.BlogMetadataDao
import io.vacco.mt.schema.BlogMetadata
import j8spec.J8Spec.*
import j8spec.annotation.DefinedOrder
import j8spec.junit.J8SpecRunner
import org.codejargon.fluentjdbc.api.FluentJdbc
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.h2.jdbcx.JdbcDataSource
import org.junit.Assert.*
import org.junit.runner.RunWith

@DefinedOrder
@RunWith(J8SpecRunner::class)
class MetoLitheKotlinSpec {

  var jdbc: FluentJdbc? = null
  var blogDao: BlogMetadataDao? = null
  var dbUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"

  init {
    beforeAll {
      val ds = JdbcDataSource()
      ds.setURL(dbUrl)
      jdbc = FluentJdbcBuilder().connectionProvider(ds).build()
      blogDao = BlogMetadataDao(jdbc!!, NativeBase64Codec(), "public")
      assertNotNull(blogDao)
    }
    it("Can add a new data object") {
      var bm = BlogMetadata("ABCDEF", "I am the great gopher.",
          setOf("fun", "lol", "OMGRLY"), BlogMetadata.PublishStatus.SCHEDULED)
      bm =  blogDao!!.merge(bm)
      assertNotNull(bm)
    }
    it("Can read back an existing data object") {
      var bm = blogDao!!.loadExisting("ABCDEF")
      val t0 = "This is how we do sh*t at the nursing home!"
      assertNotNull(bm)
      bm = blogDao!!.merge(bm.copy(title = t0))
      assertEquals(t0, bm.title)
    }
  }

}