package io.vacco.metolithe.test;

import j8spec.annotation.DefinedOrder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@DefinedOrder
@RunWith(Suite.class)
@Suite.SuiteClasses({MtAnnotationsSpec.class, MtCodeGenSpec.class, MtDaoSpec.class})
public class MtCombinedSpec {}
