package infrastructure.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class SingletonRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonRegistry.class);

    /**
     * 싱글톤 객체 등록
     * 인자로 전달되는 클래스 타입 객체를 싱글톤 컨테이너 등록한다.
     * 이미 등록되어 있는 객체를 등록하려 하는 경우, 아무 동작도 하지 않는다.
     *
     * @param clazz   클래스 타입
     * @param strategy 컨테이너에 등록되는 방식에 대한 전략
     * @throws IllegalStateException    이미 등록되어 있는 클래스를 재등록하려는 경우
     * @throws IllegalStateException    등록하려는 클래스에 {@link Singleton}어노테이션이 없는 경우
     * @throws IllegalArgumentException 등록하려는 클래스에 {@link Injection }어노테이션 또는 '기본 생성자'가 없는 경우
     */
    @SuppressWarnings("unchecked")
    protected static <T> void registerSingleton(
            Class<T> clazz,
            InjectionStrategy strategy
    ) {
        SingletonKey newKey = new SingletonKey(clazz, strategy);
        SingletonKey existingKey = SingletonContainer.findExistingKey(clazz);

        if (existingKey != null) {
            // 명시적 등록에 의한 충돌 발생시에
            if (isManualRegistrationConflict(strategy, existingKey.injectionStrategy)) {
                throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
            }
            return;
        }

        synchronized (clazz) { // 멀티스레드시 동기화 처리
            existingKey = SingletonContainer.findExistingKey(clazz);
            if (existingKey != null) {
                if (isManualRegistrationConflict(strategy, existingKey.injectionStrategy)) {
                    throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
                }
                return;
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

                SingletonContainer.put(newKey, instance); // 인스턴스 저장
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
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
    public static <T> void registerInstanceAsSingleton(
            Class<T> clazz,
            T instance
    ) {
        SingletonKey key = new SingletonKey(clazz, InjectionStrategy.MANUAL_INJECTED);
        if (SingletonContainer.containsKey(clazz)) {
            throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
        }

        synchronized (clazz) {
            if (SingletonContainer.containsKey(clazz)) {
                throw new IllegalStateException("Is already registered as a singleton. Class: " + clazz.getName());
            }
            SingletonContainer.put(key, instance);
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

    private static boolean isManualRegistrationConflict(
            InjectionStrategy newStrategy,
            InjectionStrategy existingStrategy
    ) {
        return newStrategy == InjectionStrategy.MANUAL_INJECTED
                || existingStrategy == InjectionStrategy.MANUAL_INJECTED;
    }

}
