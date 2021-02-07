package mt.tools.spring.settings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class OracleDaoTest {

//    public GenericContainer redis = new GenericContainer("redis:5.0.3-alpine").withExposedPorts(6379);

    @Rule
    public GenericContainer redis = new GenericContainer("store/oracle/database-enterprise:12.2.0.1").withExposedPorts(6379);


    @Before
    public void setUp() {
    }

    @Test
    public void testKuce() {

    }
}
