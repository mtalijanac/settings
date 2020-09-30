package mt.tools.spring.settings;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class SettingRowMapper implements RowMapper<Setting> {

    @Override
    public Setting mapRow(ResultSet rs, int rowNum) throws SQLException {
        String preferenceName = rs.getString("preferenceName");
        String value = rs.getString("value");
        String type = rs.getString("type");
        String status = rs.getString("status");
        String description = rs.getString("description");
        String defValue = rs.getString("defaultValue");

        Setting s = new Setting();
        s.setPreferenceName(preferenceName);
        s.setValue(value);
        s.setType(type);
        s.setStatus(status);
        s.setDescription(description);
        s.setDefaultValue(defValue);

        return s;
    }

}
