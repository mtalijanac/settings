package mt.tools.spring.settings;

import static org.springframework.util.StringUtils.isEmpty;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class H2SettingsDao extends SettingsDaoBase {


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




}
