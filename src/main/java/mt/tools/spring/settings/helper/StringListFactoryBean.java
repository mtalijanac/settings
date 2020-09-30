package mt.tools.spring.settings.helper;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import lombok.Getter;
import lombok.Setter;

/**
 * Parsira vrijednosti odvojene zarezom ili to�ka zarezo i kreira listu istih.
 */
public class StringListFactoryBean implements FactoryBean<List<String>> {

    @Getter boolean singleton = false;
    @Getter Class<?> objectType = List.class;

    @Setter String value;
    @Setter String seperatorChars = " ,;";

    @Override
    public List<String> getObject() {
        String regex = "[\\s" + seperatorChars + "]+";
        String trimmed = value.replaceAll(regex, " ");
        String[] splitted = StringUtils.split(trimmed, seperatorChars);
        List<String> result = Arrays.asList(splitted);
        return result;

    }

}
