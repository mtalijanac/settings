package mt.tools.spring.settings;

import java.util.Set;

public interface Converter<T> {

	Class<T> getObjectType();

	Set<String> getSupportedTypes();

	default String toTextValue(T object) {
		return object == null ? null : object.toString();
	}

	T toObject(String textValue);


	default boolean supports(String type) {
		Set<String> supportedTypes = getSupportedTypes();
		for (String stype: supportedTypes) {
			boolean matched = stype.equalsIgnoreCase(type);
			if (matched) {
				return true;
			}
		}
		return false;
	}
}
