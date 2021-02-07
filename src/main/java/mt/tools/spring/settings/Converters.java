package mt.tools.spring.settings;

import static java.util.Collections.unmodifiableSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Converters {

    static List<Converter<? extends Object>> defaultConverters() {
        List<Converter<? extends Object>> res = Arrays.asList(
                new BooleanConverter(),
                new ByteConverter(),
                new ShortConverter(),
                new IntegerConverter(),
                new LongConverter(),
                new FloatConverter(),
                new DoubleConverter(),
                new CharacterConverter(),
                new StringConverter()
        );
        return res;
    }


    public static class BooleanConverter implements Converter<Boolean> {
        @Getter Class<Boolean> objectType = Boolean.class;
        String[] types = {"bool", "boolean", "java.lang.Boolean"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Boolean toObject(String textValue) {
            return Boolean.parseBoolean(textValue);
        }
    }

    public static class ByteConverter implements Converter<Byte> {
        @Getter Class<Byte> objectType = Byte.class;
        String[] types = {"byte", "java.lang.Byte"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Byte toObject(String textValue) {
            return Byte.decode(textValue);
        }
    }

    public static class ShortConverter implements Converter<Short> {
        @Getter Class<Short> objectType = Short.class;
        String[] types = {"short", "java.lang.Short"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Short toObject(String textValue) {
            return Short.decode(textValue);
        }
    }

    public static class IntegerConverter implements Converter<Integer> {
        @Getter Class<Integer> objectType = Integer.class;
        String[] types = {"int", "integer", "java.lang.Integer"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Integer toObject(String textValue) {
            return Integer.decode(textValue);
        }
    }

    public static class LongConverter implements Converter<Long> {
        @Getter Class<Long> objectType = Long.class;
        String[] types = {"long", "java.lang.Long"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Long toObject(String textValue) {
            return Long.decode(textValue);
        }
    }

    public static class FloatConverter implements Converter<Float> {
        @Getter Class<Float> objectType = Float.class;
        String[] types = {"float", "java.lang.Float"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Float toObject(String textValue) {
            return Float.parseFloat(textValue);
        }
    }

    public static class DoubleConverter implements Converter<Double> {
        @Getter Class<Double> objectType = Double.class;
        String[] types = {"double", "java.lang.Double"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Double toObject(String textValue) {
            return Double.parseDouble(textValue);
        }
    }

    public static class CharacterConverter implements Converter<Character> {
        @Getter Class<Character> objectType = Character.class;
        String[] types = {"char", "character", "java.lang.Character"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public Character toObject(String textValue) {
            return textValue.charAt(0);
        }
    }

    public static class StringConverter implements Converter<String> {
        @Getter Class<String> objectType = String.class;
        String[] types = {"string", "java.lang.String"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public String toObject(String textValue) {
            return textValue;
        }
    }

    @RequiredArgsConstructor
    public static class SecretConverter implements Converter<String> {
        final Secrets secrets;

        @Getter Class<String> objectType = String.class;
        String[] types = {"secret", "password"};
        @Getter Set<String> supportedTypes = unmodifiableSet(new HashSet<>(Arrays.asList(types)));

        public String toObject(String textValue) {
            return secrets.decrypt(textValue);
        }
    }

}
