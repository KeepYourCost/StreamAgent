package infrastructure.singleton;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

public class SingletonScanner {
    private static final Map<Class<?>, Object> container = SingletonContainer.instances;

    /**
     * 싱글톤 객체 등록
     * 인자로 전달되는 클래스 타입 객체를 싱글톤 컨테이너 등록한다.
     * 이미 등록되어 있는 객체를 등록하려 하는 경우, 아무 동작도 하지 않는다.
     */
    public static void scanSingletonsFromRoot() {
        try {
            // Root 패키지는 ""으로 사용
            scanSingletonsInPackage("");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
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
            // 각 클래스를 컨테이너에 등록
            try {
                registerSingleton(clazz);
            } catch (IllegalStateException ex) {
                if (ex.getMessage().startsWith("Is already registered")) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    /**
     * 싱글톤 객체 등록
     * 인자로 전달되는 클래스 타입 객체를 싱글톤 컨테이너 등록한다.
     * 이미 등록되어 있는 객체를 등록하려 하는 경우, 아무 동작도 하지 않는다.
     *
     * @param clazz 클래스 타입
     * @throws IllegalStateException 이미 등록되어 있는 클래스를 재등록하려는 경우
     * @throws IllegalStateException 등록하려는 클래스에 {@link Singleton}어노테이션이 없는 경우
     * @throws IllegalArgumentException 등록하려는 클래스에 {@link Injection }어노테이션 또는 '기본 생성자'가 없는 경우
     */
    @SuppressWarnings("unchecked")
    public static <T> void registerSingleton(Class<T> clazz) {
        if (container.containsKey(clazz)) {
            throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
        }

        synchronized (clazz) { // 멀티스레드시 동기화 처리
            if (container.containsKey(clazz)) {
                throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
            }

            try {
                // Singleton 어노테이션 확인
                if (!clazz.isAnnotationPresent(Singleton.class)) {
                    throw new IllegalStateException("Is not annotated with '@Singleton'. Class: " + clazz.getName());
                }
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();

                // 주입할 생성자 조회
                Constructor<T> injectionConstructor = findConstructorWithInjection(constructors);
                if (injectionConstructor == null) {
                    throw new IllegalArgumentException("Failed found suitable constructor. Class: " + clazz.getName());
                }

                // reflection 접근제어자 허용
                injectionConstructor.setAccessible(true);

                // 주입될 parameters 배열 생성
                Object[] parameters = createInjectionParameters(injectionConstructor);
                T instance = injectionConstructor.newInstance(parameters);

                container.put(clazz, instance); // 인스턴스 저장
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                throw new RuntimeException("Failed to create singleton instance", ex);
            }

        }
    }

    /**
     * 외부에서 생성된 객체를 싱글톤 컨테이너에 등록한다.
     * 이미 해당 클래스 타입으로 등록된 싱글톤 객체가 있을 경우, 예외가 발생한다.
     *
     * @param clazz 클래스 타입
     * @param instance 해당 클래스 타입의 인스턴스
     * @throws IllegalStateException 이미 등록되어 있는 클래스를 재등록하려는 경우
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
        
        // 재귀적으로 호출하여, 싱글톤 객체 조회
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = SingletonFactory.getInstance(parameterTypes[i]);
        }
        return parameters;
    }
}
