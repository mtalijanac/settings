package mt.tools.spring.settings;

import java.util.List;

import lombok.Setter;

public class SettingsService {

    @Setter SettingsDao settingsDao;
    @Setter Secrets secrets;

    @Setter List<Converter<? extends Object>> converters = Converters.defaultConverters();

    Cache cache = new Cache();

    public List<Setting> findByExample(Setting example, String jtSorting, Long jtStartIndex, Long endRow) {
        List<Setting> settings = settingsDao.findByExample(example, jtSorting, jtStartIndex, endRow);
        for (Setting set: settings) {
            cache.put(set);
        }
        return settings;
    }

    public void cacheAll() {
        List<Setting> settings = settingsDao.findByExample(new Setting(), null, null, null);
        for (Setting set: settings) {
            cache.put(set);
        }
    }

    public Setting findCached(String preferenceName) {
        Setting setting = cache.get(preferenceName);
        if (setting != null) {
            return setting;
        }

        Setting settingdb = settingsDao.findByName(preferenceName);
        if (settingdb == null) {
            return null;
        }

        cache.put(settingdb);
        return settingdb;
    }

    public Long countByExample(Setting example) {
        return settingsDao.countByExample(example);
    }

    public int createSetting(Setting setting) {
        cache.put(setting);
        return settingsDao.createSetting(setting);
    }

    public int deleteSetting(String preferenceName) {
        cache.remove(preferenceName);
        return settingsDao.deleteSetting(preferenceName);
    }

    public int updateSettings(Setting updatedSetting) {
        cache.remove(updatedSetting.getPreferenceName());
        cache.put(updatedSetting);
        return settingsDao.updateSettings(updatedSetting);
    }

    public String decrypt(String cipherText) {
        String plainText = secrets.decrypt(cipherText);
        return plainText;
    }

    public String encrypt(String plainText) {
        String cipherText = secrets.encrypt(plainText);
        return cipherText;
    }

    public SettingFactoryBean newValue(String defaultValue, String type, String description) {
        SettingFactoryBean sfb = new SettingFactoryBean();
        sfb.setSettingsService(this);
        sfb.setType(type);
        sfb.setDefaultValue(defaultValue);
        sfb.setDescription(description);
        return sfb;
    }


   Object convert(String value, String type) {
        if (value == null) {
            return null;
        }

        if ("".equals(value)) {
            return null;
        }

        for (Converter<?> con: converters) {
            if (con.getSupportedTypes().contains(type)) {
                Object object = con.toObject(value);
                return object;
            }
        }

        throw new UnsupportedOperationException("Unknown type: '" + type + "'");
    }

   /** @return Class of object mapped to desired type parameter or null if provided converter was not found */
   Class<?> typeClass(String type) {
       for (Converter<?> con: converters) {
           if (con.supports(type)) {
               return con.getObjectType();
           }
       }
       return null;
   }

}
