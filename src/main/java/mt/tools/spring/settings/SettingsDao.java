package mt.tools.spring.settings;

import java.util.List;

public interface SettingsDao {

	/** @return matching settings */
	List<Setting> findByExample(Setting example, String orderBy, Long startRow, Long endRow);

	/* @return number of matching rows */
	Long countByExample(Setting example);

	/** @return the number of rows affected */
	int deleteByExample(Setting example);

	/** @return the number of rows affected */
	int createSetting(Setting setting);

	/** @return the number of rows affected */
	int deleteSetting(String preferenceName);

	/** @return the number of rows affected */
	int updateSettings(Setting updatedSetting);

}
