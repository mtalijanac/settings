package mt.tools.spring.settings;

import java.util.HashMap;

class Cache {
    HashMap<String, Object[]> cache = new HashMap<>();
    long maxAgeInMs = 60_000;

    void remove(String name) {
        cache.remove(name);
    }

    void clear() {
        cache.clear();
    }

    Setting get(String preferenceName) {
        Object[] cached = cache.get(preferenceName);
        if (cached == null) {
            return null;
        }

        Long tentry = (Long) cached[1];
        long age = System.currentTimeMillis() - tentry;
        if (age > maxAgeInMs) {
            cache.remove(preferenceName);
            return null;
        }

        Setting result = (Setting) cached[0];
        return result;
    }

    void put(Setting setting) {
        Long tentry = System.currentTimeMillis();
        Object[] value = {setting, tentry};
        String key = setting.getPreferenceName();
        cache.put(key, value);
    }
}
