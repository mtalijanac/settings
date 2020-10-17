package mt.tools.spring.settings;

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
            // first init in memory h2 database by grabing one connection and creating db
            String dburl = "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:testdb.h2.sql'";
            JdbcDataSource initDs = new JdbcDataSource();
            initDs.setURL(dburl);
            Connection connection = initDs.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("SELECT COUNT(*) FROM TEST_SETTINGS");

            // than create ds to the created daatabase without INIT part
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL("jdbc:h2:mem:db1");
            return ds;
        }

        @Bean
        NamedParameterJdbcTemplate jdbcTemplate() throws SQLException {
        	return new NamedParameterJdbcTemplate(dataSource());
        }

        @Bean
        H2SettingsDao settingsDao() throws SQLException {
        	H2SettingsDao dao = new H2SettingsDao();
        	dao.setJdbcTemplate(jdbcTemplate());
        	dao.setTablename("TEST_SETTINGS");
        	return dao;
        }

        @Bean(name = "anExample")
        SettingFactoryBean anExample(SettingsDao settingsDao) throws Exception {
        	SettingFactoryBean sfb = new SettingFactoryBean();
        	sfb.setSettingsDao(settingsDao);
        	sfb.setPrefix("prefix.");
        	sfb.setPreferenceName("AnExample");
        	sfb.setType("string");
        	sfb.setDefaultValue("A default value for preference");
        	sfb.setDescription("This is an example of setting usage");

        	return sfb;
        }
    }
	

	@Autowired @Qualifier("anExample")
	String value;

	@Test
	public void usingAValue() {
		System.out.println(value);
	}

}
