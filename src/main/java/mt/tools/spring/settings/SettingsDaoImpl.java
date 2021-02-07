package mt.tools.spring.settings;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.Setter;

/**
 * Default SettingsDao implementation.
 * Currently supports: postgre, mysql and maria db.
 */
public class SettingsDaoImpl implements SettingsDao {

   /*
    postgre test online: https://extendsclass.com/postgresql-online.html
    mysql test online: https://paiza.io/projects/XjIuzU4pyC-CSWS4jetp8A?language=mysql

    CREATE TABLE IF NOT EXISTS SETTINGS(
        PREFERENCENAME VARCHAR(100) NOT NULL,      -- preference name
        VALUE VARCHAR(1000),                       -- current value of preferencec
        DEFAULTVALUE VARCHAR(1000),                -- default value
        TYPE VARCHAR(100) NOT NULL,                -- Datatype: Boolean [bool, boolean, java.lang.Boolean], integer [int, integer, java.lang.Integer], long [long, java.lang.Long], string [string, java.lang.String]
        STATUS VARCHAR(50) NOT NULL,               -- status: DEFAULT | USER SET
        DESCRIPTION VARCHAR(500),                  -- preference description

        PRIMARY KEY(PREFERENCENAME)
    );
   */

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


    protected Object[] queryByExample(final String what, final Setting example, final String orderBy, final Long startRow, final Long endRow) {
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

        if (startRow != null && endRow != null) {
            long pageSize = endRow - startRow;
            qb.append(" LIMIT ").append(pageSize).append(" OFFSET ").append(startRow);
        }

        String query = qb.toString().replaceFirst(" AND ", " WHERE ");

        return new Object[] {query, ps};
    }


    public Setting findByName(String preferenceName) {
        Setting example = new Setting();
        example.setPreferenceName(preferenceName);
        List<Setting> res = findByExample(example, null, null, null);
        if (res == null || res.isEmpty()) {
            return null;
        }

        Setting result = res.get(0);
        return result;
    }

}
