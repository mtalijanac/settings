package mt.tools.spring.settings.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import lombok.Getter;
import lombok.Setter;

/**
 * Parsira vrijednosti odvojene zarezom, to�kazarezom ili razmakom u set stringova.
 */
public class StringSetFactoryBean implements FactoryBean<Set<String>> {

    @Getter boolean singleton = false;
    @Getter Class<?> objectType = Set.class;

    @Setter String value;
    @Setter String seperatorChars = " ,;";

    @Override
    public Set<String> getObject() {
        String regex = "[\\s" + seperatorChars + "]+";
        String trimmed = value.replaceAll(regex, " ");
        String[] splitted = StringUtils.split(trimmed, " ");

        HashSet<String> result = new HashSet<>(Arrays.asList(splitted));
        return result;
    }

}
