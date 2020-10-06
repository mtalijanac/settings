package mt.tools.spring.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
public class SettingsServletContextListener implements ServletContextListener {
    /**
     * Comma separated list of tables used to store setting values.
     */
    @Setter String settingTables = "Settings";

    @Setter String datasourceName;

    @Setter String servletContextName = "SETTINGS";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // 10. fetch datasource
        // 20. init jdbctemplate, settings dao for each dbtable
        // 30. read settings in a map<string,string>
        // 40. store all settings as Map<tablename, map<preferenceName,value>> in the servlet context under attribute 'ALL_SETTINGS'

        ServletContext servletContext = event.getServletContext();
        String configuredSettingTables = fetchInitiParameter(servletContext, "Settings-tables", settingTables);
        String configuredDatasourceName = fetchInitiParameter(servletContext, "Settings-datasourceName", datasourceName);

        String[] tables = configuredSettingTables.replaceAll("\\s+", "").split(",");
        log.debug("Settings tables used: '{}'", Arrays.toString(tables));

        DataSource dataSource = datasourceLookup(configuredDatasourceName);
        if (dataSource == null) {
            log.error("No datasource found for settings lookup. datasourceName: '{}'", dataSource);
            throw new RuntimeException("No datasource found for settings lookup");
        }

        Map<String, Map<String, String>> allSettings = new HashMap<String, Map<String,String>>();
        for (String tablename: tables) {
            OracleSettingsDao dao = initDao(tablename, dataSource);
            Setting example = new Setting();
            Map<String, String> values = dao.findSimplifiedSettings(example);
            log.info("Loaded '{}' settings from table: '{}'", values.size(), tablename);

            allSettings.put(tablename, values);
        }

        servletContext.setAttribute("ALL_SETTINGS", allSettings);
    }

    String fetchInitiParameter(ServletContext servletContext, String parameterName, String defaultValue) {
        String value = (String) servletContext.getInitParameter(parameterName);
        if (value != null) {
            log.debug("Settings found config value: '{}' under parameterName: '{}'", value, parameterName);
            return value;
        }

        log.debug("No config value found for parameterName: '{}'. Using default: '{}'", parameterName, defaultValue);
        return defaultValue;
    }

    DataSource datasourceLookup(String datasourceName) {
        try {
            Context env = (Context)new InitialContext().lookup("java:comp/env");
            DataSource ds = (DataSource) env.lookup(datasourceName);
            return ds;
        }
        catch (NamingException e) {
            log.error("NamingException while looking up for datasource '{}'", datasourceName, e);
            return null;
        }
    }

    OracleSettingsDao initDao(String tablename, DataSource dataSource) {
        NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(dataSource);
        OracleSettingsDao settingsDao = new OracleSettingsDao();
        settingsDao.setJdbcTemplate(npjt);
        settingsDao.setTablename(tablename);
        return settingsDao;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

}
