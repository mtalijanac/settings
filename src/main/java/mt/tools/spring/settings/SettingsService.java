package mt.tools.spring.settings;

import java.util.List;

import lombok.Setter;

public class SettingsService {

    @Setter SettingsDao settingsDao;

    public List<Setting> findByExample(Setting example, String jtSorting, Long jtStartIndex, Long endRow) {
        List<Setting> settings = settingsDao.findByExample(example, jtSorting, jtStartIndex, endRow);
        return settings;
    }

    public Long countByExample(Setting example) {
        return settingsDao.countByExample(example);
    }

    public int createSetting(Setting setting) {
        return settingsDao.createSetting(setting);
    }

    public int deleteSetting(String preferenceName) {
        return settingsDao.deleteSetting(preferenceName);
    }

    public int updateSettings(Setting updatedSetting) {
        return settingsDao.updateSettings(updatedSetting);
    }

}
