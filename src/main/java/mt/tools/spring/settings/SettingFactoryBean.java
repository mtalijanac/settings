package mt.tools.spring.settings;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

/**
 * FactoryBean used to load object to spring context. <br>
 *
 * If corresponding text value is found in a database, it is used to create a instance of object.
 * If value is not present in database, a new value is added to database and than it is used to create instance of the object.
 *
 * @see FactoryBean
 */
@CommonsLog
public class SettingFactoryBean implements FactoryBean<Object>, InitializingBean, BeanNameAware, EnvironmentAware {
    @Setter Environment environment;
    @Getter boolean singleton = false;
    @Getter Class<?> objectType;

    @Setter SettingsService settingsService;
    @Setter Properties defaultOverrides;

    @Setter String beanName;
    @Setter String preferenceName;
    @Setter String prefix;

    @Setter String defaultValue;
    @Setter String type;
    @Setter String description;



    @Override
    public void afterPropertiesSet() throws Exception {
        if (preferenceName == null) {
            preferenceName = beanName;
        }

        if (prefix != null) {
            preferenceName = prefix + preferenceName;
        }

        objectType = settingsService.typeClass(type);
        if (objectType == null) {
            throw new IllegalStateException("Unsupported type: '" + type + "' for preferenceName: '" + preferenceName + "'");
        }
    }

    @Override
    public Object getObject() throws Exception {
        Setting setting = settingsService.findCached(preferenceName);
        if (setting == null) {
            setting = storeSetting();
        }

        String textValue = setting.getValue();
        log.info("Loaded setting: '" + setting.getPreferenceName() + "' with value: '" + textValue + "'");
        String type = setting.getType();
        Object objectValue = settingsService.convert(textValue, type);
        publishToEnv(preferenceName, objectValue);
        return objectValue;
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

        settingsService.createSetting(def);
        return def;
    }


    void publishToEnv(String name, Object value) {
        if (environment == null) {
            return;
        }

        if (!(environment instanceof ConfigurableEnvironment)) {
            return;
        }

        ConfigurableEnvironment cenv = (ConfigurableEnvironment) environment;
        MutablePropertySources propertySources = cenv.getPropertySources();

        String ps_key = "SETTINGS_PSNAME";
        synchronized (SettingsServiceFactory.class) {
            if (!propertySources.contains(ps_key)) {
                propertySources.addFirst(new MapPropertySource(ps_key, new ConcurrentHashMap<>()));
            }
        }

        PropertySource<?> ps = propertySources.get(ps_key);
        Map<String, Object> settingMap =  (Map<String, Object>) ps.getSource();
        settingMap.put(name, value);
    }

}
