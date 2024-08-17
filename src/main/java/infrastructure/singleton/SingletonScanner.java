package infrastructure.singleton;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

public class SingletonScanner {
    private static final Map<Class<?>, Object> container = SingletonContainer.instances;

    public static void scanSingletonsFromRoot() {
        // Root 패키지는 ""으로 사용
        scanSingletonsInPackage("");
    }

    public static void scanSingletonsInPackage(String packageName) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(packageName)
                        .addScanners(Scanners.TypesAnnotated)
        );

        // @Singleton 어노테이션이 붙은 모든 클래스 조회
        Set<Class<?>> singletonClasses = reflections.getTypesAnnotatedWith(Singleton.class);

        for (Class<?> clazz : singletonClasses) {
            // 각 클래스의 인스턴스를 생성하여 컨테이너에 등록
            SingletonFactory.getInstance(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void registerSingleton(Class<T> clazz) {
        if (container.containsKey(clazz)) {
            return;
        }

        synchronized (clazz) { // 멀티스레드시 동기화 처리
            if (container.containsKey(clazz)) {
                return;
            }

            try {
                // Singleton 어노테이션 확인
                if (!clazz.isAnnotationPresent(Singleton.class)) {
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

                container.put(clazz, instance); // 인스턴스 저장
            } catch (Exception e) {
                throw new RuntimeException("Failed to create singleton instance", e);
            }

        }
    }

    /**
     * 외부에서 생성된 객체를 싱글톤 컨테이너에 등록한다.
     * 이미 해당 클래스 타입으로 등록된 싱글톤 객체가 있을 경우, 예외가 발생한다.
     *
     * @param clazz 클래스 타입
     * @param instance 해당 클래스 타입의 인스턴스
     * @throws IllegalStateException 이미 존재하는 클래스 등록하려 할 경우 발생
     */
    public static <T> void registerInstanceAsSingleton(Class<T> clazz, T instance) {
        if (container.containsKey(clazz)) {
            throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
        }

        synchronized (clazz) {
            if (container.containsKey(clazz)) {
                throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
            }
            container.put(clazz, instance);
        }
    }

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
            parameters[i] = SingletonFactory.getInstance(parameterTypes[i]);
        }
        return parameters;
    }
}
