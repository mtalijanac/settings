package mt.tools.spring.settings;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Setter List<Converter<? extends Object>> converters = Converters.defaultConverters();
    @Setter SettingsDao settingsDao;
    @Setter Properties defaultOverrides;

    @Setter String beanName;
    @Setter String preferenceName;
    @Setter String prefix;

    @Setter String defaultValue;
    @Setter String type;
    @Setter String description;



    // properties used for cached loading of settings:
    Map<String, Setting> cachedValues;
    Long cachedValuesTimestamp;
    @Setter Long cachedValuesDurationInMs = 30000L;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (preferenceName == null) {
            preferenceName = beanName;
        }

        if (prefix != null) {
            preferenceName = prefix + preferenceName;
        }

        for (Converter<?> con: converters) {
        	if (con.supports(type)) {
        		objectType = con.getObjectType();
        	}
        }

        if (objectType == null) {
        	throw new IllegalStateException("Unsupported type: '" + type + "' for preferenceName: '" + preferenceName + "'");
        }
    }


    @Override
    public Object getObject() throws Exception {
    	Map<String, Setting> cachedSetting = cachedSetting();
    	if (cachedSetting != null) {
    		Setting cached = cachedSetting.get(preferenceName);
    		if (cached != null) {
    			return cached;
    		}
    	}

        Setting localSetting = loadSetting();
        String value = localSetting.getValue();
        String type = localSetting.getType();
        return convert(value, type);
    }


    Map<String, Setting> cachedSetting() {
    	if (cachedValuesTimestamp == null) {
    		List<Setting> res = settingsDao.findByExample(new Setting(), null, null, null);
    		cachedValues = res.stream().collect(Collectors.toMap(Setting::getPreferenceName, Function.identity()));
    		cachedValuesTimestamp = System.currentTimeMillis();
    		return cachedValues;
    	}

    	long now = System.currentTimeMillis();
    	long expiry = cachedValuesTimestamp + cachedValuesDurationInMs;

    	if (now <= expiry) {
    		return cachedValues;
    	}

    	return null;
    }


    synchronized Setting loadSetting() {
    	Setting example = new Setting();
    	example.setPreferenceName(preferenceName);

    	List<Setting> res = settingsDao.findByExample(example, null, null, null);
    	if (res.isEmpty()) {
    		Setting setting = storeSetting();
    		String msg = "No setting value for name: '" + preferenceName + "'. Stored and using value: '" + setting.getValue() + "'.";
            log.info(msg);
            return setting;
    	}

    	if (res.size() > 1) {
    		log.error("Multiple result found for preferencename: '" + preferenceName + "'");
    	}

    	Setting setting = res.get(0);
        String msg = "Loaded setting value for name: '" + preferenceName + "', loaded value: '" + setting.getValue() + "'.";
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

        for (Converter<?> con: converters) {
        	if (con.supports(type)) {
        		return con.toObject(value);
        	}
        }

        throw new UnsupportedOperationException();
    }

}
