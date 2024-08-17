package infrastructure.singleton;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class SingletonScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonScanner.class);

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
            LOGGER.error(ex.getMessage());
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
                SingletonRegistry.registerSingleton(clazz, InjectionStrategy.AUTO_INJECTED);
            } catch (IllegalStateException ex) {
                if (ex.getMessage().startsWith("Is already registered")) {
                    LOGGER.warn(ex.getMessage());
                    return;
                }
                LOGGER.error(ex.getMessage());
            }
        }
    }
}
