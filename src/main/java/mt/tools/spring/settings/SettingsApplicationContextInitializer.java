package mt.tools.spring.settings;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;

@Slf4j
public class SettingsApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    @Override
    public void initialize(ConfigurableWebApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        ConfigurableEnvironment confEnv = ConfigurableEnvironment.class.cast(environment);
        MutablePropertySources envPropertySources = confEnv.getPropertySources();

        if (!(environment instanceof ConfigurableEnvironment)) {
            throw new RuntimeException("Can't add Settings to Spring context Environment");
        }

        ServletContext servletContext = applicationContext.getServletContext();
        Map<String, Map<String, Object>> allSettings = (Map<String, Map<String, Object>>) servletContext.getAttribute("ALL_SETTINGS");

        if (allSettings == null) {
            log.warn("No settings found in server context");
            return;
        }

        for (Entry<String, Map<String, Object>> entry: allSettings.entrySet()) {
            String tablename = entry.getKey();
            Map<String, Object> settings = entry.getValue();

            PropertySource<?> settingsPropertySource = new MapPropertySource(tablename, settings);
            envPropertySources.addFirst(settingsPropertySource);

            log.info("Added '{}' value from settings '{}' to spring Environment", settings.size(), tablename);
        }
    }

}
