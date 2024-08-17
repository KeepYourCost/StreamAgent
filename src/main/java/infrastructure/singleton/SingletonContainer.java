package infrastructure.singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// package에서만 접근가능
class SingletonContainer {
    protected static final Map<SingletonKey, Object> instances = new ConcurrentHashMap<>();

    protected static SingletonKey findExistingKey(Class<?> clazz) {
        for (SingletonKey key : instances.keySet()) {
            if (key.aClass.equals(clazz)) {
                return key;
            }
        }
        return null;
    }

    protected static boolean containsKey(Class<?> clazz) {
        return findExistingKey(clazz) != null;
    }

    protected static Object get(Class<?> clazz) {
        SingletonKey key = findExistingKey(clazz);
        return key != null ? instances.get(key) : null;
    }

    protected static void put(SingletonKey key, Object instance) {
        instances.put(key, instance);
    }
}
