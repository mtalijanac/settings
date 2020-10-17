package mt.tools.spring.settings;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class OracleSettingsDao implements SettingsDao {

    @Setter NamedParameterJdbcTemplate jdbcTemplate;
    @Setter String tablename = "Settings";
    @Setter SettingRowMapper settingRowMapper = new SettingRowMapper();

    @Setter long maximumAgeInMs = 10 * 1000;
    HashMap<String, Object[]> cache = new HashMap<String, Object[]>();
    long lastPrefetchTime;

    public List<Setting> findByExample(Setting example, final String orderBy, final Long startRow, final Long endRow) {
        Object[] queryAndPs = queryByExample("SELECT *", example, orderBy, startRow, endRow);
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

    Object[] queryByExample(final String what, final Setting example, final String orderBy, final Long startRow, final Long endRow) {
        StringBuilder qb = new StringBuilder(what).append(" FROM ").append(tablename);
        MapSqlParameterSource ps = new MapSqlParameterSource();

        if (!isEmpty(example.getPreferenceName())) {
            qb.append(" AND preferenceName LIKE :preferenceName");
            ps.addValue("preferenceName", example.getPreferenceName());
        }

        if (!isEmpty(example.getValue())) {
            qb.append(" AND value LIKE :value");
            ps.addValue("value", example.getValue());
        }

        if (!isEmpty(example.getType())) {
            qb.append(" AND type LIKE :type");
            ps.addValue("type", example.getType());
        }

        if (!isEmpty(example.getStatus())) {
            qb.append(" AND status LIKE :status");
            ps.addValue("status", example.getStatus());
        }

        if (!isEmpty(example.getDescription())) {
            qb.append(" AND description LIKE :description");
            ps.addValue("description", example.getDescription());
        }

        if (!isEmpty(example.getDefaultValue())) {
            qb.append(" AND defaultValue LIKE :defaultValue");
            ps.addValue("defaultValue", example.getDefaultValue());
        }

        if (orderBy != null) {
            qb.append(" ORDER BY ").append(orderBy);
        }

        String query = qb.toString().replaceFirst(" AND ", " WHERE ");

        if (startRow != null && endRow != null) {
            query = String.format("SELECT * FROM ( SELECT ROWNUM rnum, q.* FROM ( %s ) q WHERE rownum < :endRow ) WHERE rnum >= :startRow", query);
            ps.addValue("startRow", startRow);
            ps.addValue("endRow", endRow);
        }

        return new Object[] {query, ps};
    }

    public Map<String, String> findSimplifiedSettings(Setting example) {
        List<Setting> settings = findByExample(example, null, null, null);

        Map<String, String> result = new HashMap<String, String>();
        for (Setting set: settings) {
            String name = set.getPreferenceName();
            String value = set.getValue();
            result.put(name, value);
        }
        return result;
    }

    public int createSetting(Setting setting) {
        String query = "INSERT INTO " + tablename + "(preferenceName, value, type, status, description, defaultValue) VALUES(:preferenceName, :value, :type, :status, :description, :defaultValue)";
        BeanPropertySqlParameterSource ps = new BeanPropertySqlParameterSource(setting);
        int update = jdbcTemplate.update(query, ps);
        return update;
    }

    public int deleteSetting(final String preferenceName) {
        String query = "DELETE FROM " + tablename + " WHERE preferenceName = ?";
        int rowsAffected = jdbcTemplate.getJdbcOperations().update(query, preferenceName);
        return rowsAffected;
    }

    public int updateSettings(Setting updatedSetting) {
        String query = "UPDATE " + tablename + " SET value = :value, type = :type, status = :status, description = :description, defaultValue = :defaultValue WHERE preferenceName = :preferenceName";
        BeanPropertySqlParameterSource ps = new BeanPropertySqlParameterSource(updatedSetting);
        int updatedRowCount = jdbcTemplate.update(query, ps);
        return updatedRowCount;
    }

}
