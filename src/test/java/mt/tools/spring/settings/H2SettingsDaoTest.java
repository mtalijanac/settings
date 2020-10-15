package mt.tools.spring.settings;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class H2SettingsDaoTest {

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
        H2SettingsDao dao() throws SQLException {
        	H2SettingsDao dao = new H2SettingsDao();
        	dao.setJdbcTemplate(jdbcTemplate());
        	dao.setTablename("TEST_SETTINGS");
        	return dao;
        }

    }

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	H2SettingsDao dao;

	@Test
	public void create() {
		SqlParameterSource params = new MapSqlParameterSource();
		Long dbRowsCountBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TEST_SETTINGS", params, Long.class);

		Setting setting = new Setting();
		setting.setPreferenceName("created_test_value");
		setting.setDefaultValue("a some test value");
		setting.setType("java.lang.string");
		setting.setStatus("DEFAULT");
		setting.setDescription("Made in H2SettingsDaoTest#createTestSetting test");

		int insertCount = dao.createSetting(setting);

		Assert.assertEquals(1, insertCount);

		Long dbRowsCountAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TEST_SETTINGS", params, Long.class);
		long diff = dbRowsCountAfter - dbRowsCountBefore;
		Assert.assertEquals(1l, diff);
	}

	@Test
	public void createAndFind() {
		Random random = new Random();
		String prefName = "pref_" + random.nextInt();

		Setting expactedSetting = new Setting();
		expactedSetting.setPreferenceName(prefName);
		expactedSetting.setDefaultValue("a some test value");
		expactedSetting.setType("java.lang.string");
		expactedSetting.setStatus("DEFAULT");
		expactedSetting.setDescription("Made in H2SettingsDaoTest#createAndLoadSetting");

		dao.createSetting(expactedSetting);

		Setting example = new Setting();
		example.setPreferenceName(prefName);

		List<Setting> settings = dao.findByExample(example, null, null, null);
		Assert.assertEquals(1, settings.size());

		Setting actualSetting = settings.get(0);
		Assert.assertEquals(expactedSetting, actualSetting);
	}

	@Test
	public void testCountAndPaging() {
		int settingCount = 113;
		for (int i = 1; i <= settingCount; i++) {
			Setting expactedSetting = new Setting();
			expactedSetting.setPreferenceName("paging_test_" + i);
			expactedSetting.setDefaultValue("a some test value");
			expactedSetting.setType("java.lang.string");
			expactedSetting.setStatus("DEFAULT");
			expactedSetting.setDescription("Made in H2SettingsDaoTest#testPaging");

			dao.createSetting(expactedSetting);
		}

		Setting example = new Setting();
		example.setPreferenceName("paging_test%");

		Long count = dao.countByExample(example);
		Assert.assertEquals(settingCount, count.longValue());

		long startRow = 0l;
		long pageSize = 10;

		ArrayList<Setting> results = new ArrayList<>();

		while (true) {
			List<Setting> res = dao.findByExample(example, "PREFERENCENAME ASC", startRow, startRow + pageSize);
			if (res == null || res.isEmpty()) {
				break;
			}

			results.addAll(res);
			startRow += pageSize;
		}

		Assert.assertEquals(settingCount, results.size());
	}


}
