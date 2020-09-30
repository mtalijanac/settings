package mt.tools.spring.settings.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import lombok.Getter;
import lombok.Setter;

/**
 * Parsira vrijednosti odvojene zarezom, to�kazarezom ili razmakom u mapu.
 * Ulazna vrijednost mora biti lista klju� vrijednost parova.
 */
public class StringMapFactoryBean implements FactoryBean<Map<String, String>> {
    @Getter boolean singleton = false;
    @Getter Class<?> objectType = Map.class;

    @Setter String value;
    @Setter String seperatorChars = " ,;";

    @Override
    public Map<String, String> getObject() {
        String regex = "[\\s" + seperatorChars + "]+";
        String trimmed = value.replaceAll(regex, " ");
        String[] splitted = StringUtils.split(trimmed, seperatorChars);
        Map<String, String> result = new HashMap<>();

        for (int idx = 0; idx < splitted.length; idx += 2) {
            String key = splitted[idx];
            String map = splitted[idx + 1];
            result.put(key, map);
        }

        return result;
    }

}
