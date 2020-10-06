package mt.tools.spring.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

/**
 * Spring FactoryBean used to load Setting value from db.
 */
@CommonsLog
public class SettingFactoryBean implements FactoryBean<Object>, InitializingBean, BeanNameAware {
    @Getter boolean singleton = false;
    @Getter Class objectType;

    @Setter OracleSettingsDao settingsDao;
    @Setter Properties defaultOverrides;

    @Setter String beanName;
    @Setter String preferenceName;
    @Setter String prefix;
    @Setter Long cacheDurationInSec = 30l;

    @Setter String defaultValue;
    @Setter String type;
    @Setter String description;


    final Map<String, Class> objectMapping = new HashMap<String, Class>(){{
        put("bool", Boolean.class);
        put("boolean", Boolean.class);
        put("java.lang.boolean", Boolean.class);

        put("int", Integer.class);
        put("integer", Integer.class);
        put("java.lang.integer", Integer.class);

        put("long", Long.class);
        put("java.lang.long", Long.class);

        put("string", String.class);
        put("java.lang.string", String.class);
    }};



    @Override
    public void afterPropertiesSet() throws Exception {
        if (preferenceName == null) {
            preferenceName = beanName;
        }

        if (prefix != null) {
            preferenceName = prefix + preferenceName;
        }

        objectType = objectMapping.get(type.toLowerCase());
    }


    @Override
    public Object getObject() throws Exception {
        Setting localSetting = loadCachedSetting();
        String value = localSetting.getValue();
        String type = localSetting.getType();
        return convert(value, type);
    }

    synchronized Setting loadCachedSetting() {
        Setting setting = settingsDao.findByName(preferenceName);
        if (setting != null) {
            String msg = "Loaded setting value for name: '" + preferenceName + "', loaded value: '" + setting.getValue() + "'.";
            log.info(msg);
            return setting;
        }

        setting = storeSetting();
        String msg = "No setting value for name: '" + preferenceName + "'. Stored and using value: '" + setting.getValue() + "'.";
        log.info(msg);
        return setting;
    }

    Setting storeSetting() {
        String defValue = defaultValue;
        if (defaultOverrides != null && defaultOverrides.containsKey(preferenceName)) {
            defValue = defaultOverrides.getProperty(preferenceName);
        }

        Setting def = new Setting();
        def.setDescription(description);
        def.setType(type);
        def.setValue(defValue);
        def.setPreferenceName(preferenceName);
        def.setStatus("DEFAULT");
        def.setDefaultValue(defValue);

        settingsDao.createSetting(def);
        return def;
    }


    Object convert(String value, String type) {
        if (value == null) {
            return null;
        }

        if ("".equals(value)) {
            return null;
        }

        if ("bool".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type) || "java.lang.Boolean".equalsIgnoreCase(type)) {
            return Boolean.parseBoolean(value);
        }

        if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type) || "java.lang.Integer".equalsIgnoreCase(type)) {
            return Integer.decode(value);
        }

        if ("long".equalsIgnoreCase(type) || "java.lang.Long".equalsIgnoreCase(type)) {
            return Long.decode(value);
        }

        if ("string".equalsIgnoreCase(type) || "java.lang.String".equalsIgnoreCase(type)) {
            return value;
        }

        throw new UnsupportedOperationException();
    }

}
