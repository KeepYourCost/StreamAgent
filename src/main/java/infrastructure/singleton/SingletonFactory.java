package infrastructure.singleton;

public class SingletonFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        if (SingletonContainer.containsKey(clazz)) {
            return (T) SingletonContainer.get(clazz);
        }

        // 인스턴스가 컨테이너에 없으면 등록
        SingletonRegistry.registerSingleton(clazz, InjectionStrategy.AUTO_INJECTED);

        // 등록된 인스턴스를 반환
        if (SingletonContainer.containsKey(clazz)) {
            return (T) SingletonContainer.get(clazz);
        }

        // 생성에 실패한 경우 예외
        throw new IllegalStateException("Failed to create or retrieve singleton instance for class: " + clazz.getName());
    }
}
