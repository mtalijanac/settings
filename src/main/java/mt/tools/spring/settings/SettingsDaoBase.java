package mt.tools.spring.settings;

import java.util.List;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.Setter;

public abstract class SettingsDaoBase implements SettingsDao {

	@Setter NamedParameterJdbcTemplate jdbcTemplate;
	@Setter String tablename = "Settings";
	@Setter SettingRowMapper settingRowMapper = new SettingRowMapper();


	@Override
	public int createSetting(Setting setting) {
		String query = "INSERT INTO " + tablename + "(preferenceName, value, type, status, description, defaultValue) VALUES(:preferenceName, :value, :type, :status, :description, :defaultValue)";
        BeanPropertySqlParameterSource ps = new BeanPropertySqlParameterSource(setting);
        int update = jdbcTemplate.update(query, ps);
        return update;
	}

	@Override
	public int deleteSetting(String preferenceName) {
		String query = "DELETE FROM " + tablename + " WHERE preferenceName = ?";
        int rowsAffected = jdbcTemplate.getJdbcOperations().update(query, preferenceName);
        return rowsAffected;
	}

	@Override
	public int updateSettings(Setting updatedSetting) {
		String query = "UPDATE " + tablename + " SET value = :value, type = :type, status = :status, description = :description, defaultValue = :defaultValue WHERE preferenceName = :preferenceName";
        BeanPropertySqlParameterSource ps = new BeanPropertySqlParameterSource(updatedSetting);
        int updatedRowCount = jdbcTemplate.update(query, ps);
        return updatedRowCount;
	}


	public List<Setting> findByExample(Setting example, final String orderBy, final Long startRowInclusive, final Long endRowExclusive) {
        Object[] queryAndPs = queryByExample("SELECT *", example, orderBy, startRowInclusive, endRowExclusive);
        String query = (String) queryAndPs[0];
        MapSqlParameterSource ps = (MapSqlParameterSource) queryAndPs[1];
        List<Setting> result = jdbcTemplate.query(query, ps, settingRowMapper);
        return result;
    }

    public Long countByExample(Setting example) {
        Object[] queryAndPs = queryByExample("SELECT COUNT(*)", example, null, null, null);
        String query = (String) queryAndPs[0];
        MapSqlParameterSource ps = (MapSqlParameterSource) queryAndPs[1];
        Long count = jdbcTemplate.queryForObject(query, ps, Long.class);
        return count;
    }

    public int deleteByExample(Setting example) {
        Object[] queryAndPs = queryByExample("DELETE", example, null, null, null);
        String query = (String) queryAndPs[0];
        MapSqlParameterSource ps = (MapSqlParameterSource) queryAndPs[1];
        int countDeleted = jdbcTemplate.update(query, ps);
        return countDeleted;
    }

    abstract Object[] queryByExample(final String what, final Setting example, final String orderBy, final Long startRowInclusive, final Long endRowExclusive);

}
