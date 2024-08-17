package infrastructure.factory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {
    // 싱글톤 인스턴스를 저장할 맵
    private static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz, Object... args) {
        if (!instances.containsKey(clazz)) {
            synchronized (clazz) { // 멀티스레드시 동기화 처리
                if (!instances.containsKey(clazz)) {
                    try {
                        // Singleton 어노테이션 확인
                        if (clazz.isAnnotationPresent(Singleton.class)) {
                            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                            Constructor<T> matchingConstructor = null;

                            // 생성자 인자 확인
                            for (Constructor<?> constructor : constructors) {
                                if (constructor.getParameterCount() == args.length) {
                                    matchingConstructor = (Constructor<T>) constructor;
                                    break;
                                }
                            }

                            // 일치하는 생성자가 없을 경우 예외
                            if (matchingConstructor == null) {
                                throw new IllegalArgumentException("No matching constructor found for class " + clazz.getName());
                            }

                            matchingConstructor.setAccessible(true); // 생성자 접근 설정
                            T instance = matchingConstructor.newInstance(args); // 인스턴스 생성
                            instances.put(clazz, instance); // 인스턴스 저장
                        } else {
                            // Singleton 어노테이션이 없을 경우 예외
                            throw new IllegalStateException("Class " + clazz.getName() + " is not annotated with @Singleton");
                        }
                    } catch (Exception e) {
                        // 인스턴스 생성 중 예외
                        throw new RuntimeException("Failed to create singleton instance", e);
                    }
                }
            }
        }
        // 싱글톤 반환
        return (T) instances.get(clazz);
    }
}
