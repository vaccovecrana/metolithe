package io.vacco.metolithe.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({MtAnnotationsSpec.class, MtCodeGenSpec.class, MtDaoSpec.class})
public class MtCombinedSpec {}
