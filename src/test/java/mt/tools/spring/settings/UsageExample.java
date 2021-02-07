package mt.tools.spring.settings;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class UsageExample {

    @Configuration
    static class TestContext {

        @Bean
        JdbcDataSource dataSource() throws SQLException {
            // first init in memory h2 database by grabbing one connection and creating db
            String dburl = "jdbc:h2:mem:usageDb;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:testdb.h2.sql'";
            JdbcDataSource initDs = new JdbcDataSource();
            initDs.setURL(dburl);
            Connection connection = initDs.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("SELECT COUNT(*) FROM TEST_SETTINGS");

            // than create ds to the new database without INIT part
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:mem:usageDb");
            return ds;
        }

        @Bean
        NamedParameterJdbcTemplate jdbcTemplate() throws SQLException {
            return new NamedParameterJdbcTemplate(dataSource());
        }

        @Bean
        SettingsServiceFactory ssFactory(NamedParameterJdbcTemplate jt) {
            SettingsServiceFactory ssf = new SettingsServiceFactory();
            ssf.setJdbcTemplate(jt);
            ssf.setTablename("TEST_SETTINGS");
            ssf.setPassword("TEST_PWD");
            return ssf;
        }


        @Bean(name = "firstString")
        SettingFactoryBean firstString(SettingsService ss) throws Exception {
            return ss.newValue("First string", "string", "This is an example of setting usage");
        }

        @Bean(name = "secondString")
        SettingFactoryBean secondString(SettingsService ss) throws Exception {
            return ss.newValue("Second string", "string", "This is an example of setting usage");
        }

        @Bean(name = "aByte")
        SettingFactoryBean aByteValue(SettingsService ss) {
            return ss.newValue("10", "byte", "Example byte value of 10");
        }

        @Bean(name="aInt")
        SettingFactoryBean intValue(SettingsService ss) {
            return ss.newValue("136", "int", "Example int value of 136");
        }

    }



    //
    // IOC examples:
    //
    @Autowired @Qualifier("firstString")
    String first;

    @Autowired @Qualifier("secondString")
    String second;

    @Autowired
    Byte aByte;

    @Autowired
    Integer aInt;



    //
    // Value examples:
    //
    @Value("#{firstString}")
    String firstValue;

    @Value("#{secondString}")
    String secondValue;

    @Value("#{aByte}")
    Byte aByteValue;

    @Value("#{aInt}")
    Integer aIntValue;



    @Test
    public void usingValues() {
        assertEquals("First string", first);
        assertEquals("Second string", second);

        Byte expectedByte = (byte) 10;
        assertEquals(expectedByte, aByte);

        Integer expectedInt = 136;
        assertEquals(expectedInt, aInt);

        assertEquals(first, firstValue);
        assertEquals(second, secondValue);
        assertEquals(aByte, aByteValue);
        assertEquals(aInt, aIntValue);
    }


}

