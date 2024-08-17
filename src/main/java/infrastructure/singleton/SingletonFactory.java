package infrastructure.singleton;

import java.util.Map;

public class SingletonFactory {
    private static final Map<Class<?>, Object> instances = SingletonContainer.instances;

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        if (instances.containsKey(clazz)) {
            return (T) instances.get(clazz);
        }

        // 인스턴스가 없으면 등록
        SingletonScanner.registerSingleton(clazz);

        // 등록된 인스턴스를 반환
        return (T) instances.get(clazz);
    }
}
