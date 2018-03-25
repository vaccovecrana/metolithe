package io.vacco.mt;

import io.vacco.mt.unit.MetoLitheKotlinSpec;
import io.vacco.mt.unit.MetoLitheSpec;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({MetoLitheSpec.class, MetoLitheKotlinSpec.class})
public class CombinedTestSuite {}
