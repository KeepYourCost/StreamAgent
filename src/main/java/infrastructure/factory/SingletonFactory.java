package infrastructure.factory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {
    // 싱글톤 인스턴스를 저장할 맵
    private static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        if (!instances.containsKey(clazz)) {
            synchronized (clazz) { // 멀티스레드시 동기화 처리
                if (!instances.containsKey(clazz)) {
                    try {
                        // Singleton 어노테이션 확인
                        if (clazz.isAnnotationPresent(Singleton.class)) {
                            throw new IllegalStateException("Class " + clazz.getName() + " is not annotated with @Singleton");
                        }
                        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                        // 주입할 생성자 조회
                        Constructor<T> injectionConstructor = findConstructorWithInjection(constructors);
                        if (injectionConstructor == null) {
                            throw new IllegalArgumentException("No suitable constructor found for class " + clazz.getName());
                        }

                        // reflection 접근제어자 허용
                        injectionConstructor.setAccessible(true);

                        // 주입될 parameters 배열 생성
                        Object[] parameters = createInjectionParameters(injectionConstructor);

                        T instance = injectionConstructor.newInstance(parameters);
                        instances.put(clazz, instance); // 인스턴스 저장

                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create singleton instance", e);
                    }
                }
            }
        }
        // 싱글톤 반환
        return (T) instances.get(clazz);
    }

    /**
     * 생성자를 전부 탐색하여 @injection 어노테이션이 붙어 있는 생성자를 찾는다.
     * 존재한다면 가장 처음 만난 Injection 생성자를 반환한다.
     * 존재하지 않는 다면, 해당 class의 기본생성자를 반환한다.
     */
    private static <T> Constructor<T> findConstructorWithInjection(Constructor<?>[] constructors) {
        Constructor<T> defaultConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Injection.class)) {
                return (Constructor<T>) constructor;
            }

            if (constructor.getParameterCount() == 0) {
                defaultConstructor = (Constructor<T>) constructor;
            }
        }
        return defaultConstructor;
    }

    private static <T> Object[] createInjectionParameters(Constructor<T> injectionConstructor) {
        Object[] parameters = new Object[injectionConstructor.getParameterCount()];
        Class<?>[] parameterTypes = injectionConstructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            // getInstance 를 재귀적으로 호출
            parameters[i] = getInstance(parameterTypes[i]);
        }
        return parameters;
    }
}
