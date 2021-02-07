package mt.tools.spring.settings;

import java.util.ArrayList;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class SettingsServiceFactory implements FactoryBean<SettingsService> {

    @Getter Class<?> objectType = SettingsService.class;
    @Getter boolean singleton = true;

    @Setter String dbDialect = "oracle";
    @Setter NamedParameterJdbcTemplate jdbcTemplate;

    @Setter String tablename = "Setting";
    @Setter String password = "CHANGE_ME";
    @Setter String salt;

    @Getter SettingsDao settingsDao;
    @Getter Secrets secrets;

    @Getter SettingsService settingsService;

    @Override
    public synchronized SettingsService getObject() {
        if (settingsService != null) {
            return null;
        }

        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("Error creating settingService. 'jdbcTemplate' property not set. Provide instance of NamedParameterJdbcTemplate.");
        }

        if ("CHANGE_ME".equals(password)) {
            log.warn("Password left to default value!! Customize password and salt if you are using Settings to store secrets!");
        }

        if (salt == null) {
            log.info("Salt not provided. Salt will be generated from password! For increased security provide your own salt value.");
        }

        settingsDao = newDao(dbDialect);

        secrets = new Secrets();
        secrets.setPassword(password);
        secrets.setSalt(salt);

        ArrayList<Converter<? extends Object>> converters = new ArrayList<>(Converters.defaultConverters());
        converters.add(new Converters.SecretConverter(secrets));

        settingsService = new SettingsService();
        settingsService.setSettingsDao(settingsDao);
        settingsService.setConverters(converters);
        settingsService.setSecrets(secrets);

        settingsService.cacheAll();
        return settingsService;
    }


    SettingsDao newDao(String dialect) {
        switch (dialect.toLowerCase()) {
        case "maria":
        case "mysql":
        case "postgresql": return defaultDialectSettingsDao();

        case "oracle":
        case "h2":         return oracleDialectSettingsDao();

        case "cassandra":
        case "sqllite":
        case "db2":
            throw new UnsupportedOperationException("Database dialect: '" + dialect + "' is not supported.");

        default:
            throw new IllegalArgumentException("Unsupported database dialect: '" + dialect + "'. Supported dialects are: 'oracle', 'h2'.");
        }
    }

    SettingsDaoImpl defaultDialectSettingsDao() {
        SettingsDaoImpl dao = new SettingsDaoImpl();
        dao.setJdbcTemplate(jdbcTemplate);
        dao.setTablename(tablename);
        return dao;
    }

    OracleSettingsDao oracleDialectSettingsDao() {
        OracleSettingsDao dao = new OracleSettingsDao();
        dao.setJdbcTemplate(jdbcTemplate);
        dao.setTablename(tablename);
        return dao;
    }

}
