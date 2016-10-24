package fmp;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.unitils.UnitilsJUnit4TestClassRunner;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public abstract class Base {
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
