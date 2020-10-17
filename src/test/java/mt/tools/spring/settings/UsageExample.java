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
        H2SettingsDao settingsDao(NamedParameterJdbcTemplate jdbcTemplate) throws SQLException {
            H2SettingsDao dao = new H2SettingsDao();
            dao.setJdbcTemplate(jdbcTemplate);
            dao.setTablename("TEST_SETTINGS");
            return dao;
        }

        SettingFactoryBean newSFB(SettingsDao settingsDao, String prefix, String preference, String type, String defaultValue, String description) {
            SettingFactoryBean sfb = new SettingFactoryBean();
            sfb.setSettingsDao(settingsDao);
            sfb.setPrefix(prefix);
            sfb.setPreferenceName(preference);
            sfb.setType(type);
            sfb.setDefaultValue(defaultValue);
            sfb.setDescription(description);
            return sfb;
        }

        @Bean(name = "firstString")
        SettingFactoryBean firstString(SettingsDao dao) throws Exception {
            return newSFB(dao, null, "aFirstExample", "string", "First string", "This is an example of setting usage");
        }

        @Bean(name = "secondString")
        SettingFactoryBean secondString(SettingsDao dao) throws Exception {
            return newSFB(dao, null, "aSecondExample", "string", "Second string", "This is an example of setting usage");
        }

        @Bean(name = "aByte")
        SettingFactoryBean aByteValue(SettingsDao dao) {
            return newSFB(dao, null, "aByteValue", "byte", "10", "Example byte value of 10");
        }

        @Bean
        SettingFactoryBean intValue(SettingsDao dao) {
            return newSFB(dao, null, "aIntValue", "int", "136", "Example int value of 136");
        }

    }


    @Autowired @Qualifier("firstString")
    String first;

    @Autowired @Qualifier("secondString")
    String second;

    @Autowired
    Byte aByte;

    @Autowired
    Integer aInt;

    @Test
    public void usingValues() {
        assertEquals("First string", first);
        assertEquals("Second string", second);

        Byte expectedByte = (byte) 10;
        assertEquals(expectedByte, aByte);

        Integer expectedInt = 136;
        assertEquals(expectedInt, aInt);
    }

}

