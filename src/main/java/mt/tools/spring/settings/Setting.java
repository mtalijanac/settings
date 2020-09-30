package mt.tools.spring.settings;

import lombok.Data;

@Data
public class Setting {

    String preferenceName;
    String status;                  // user set, default
    String type;
    String value;
    String defaultValue;
    String description;
}
