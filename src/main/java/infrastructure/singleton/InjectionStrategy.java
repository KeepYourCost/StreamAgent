package infrastructure.singleton;

public enum InjectionStrategy {
    /**
     * 내부 스캔에 의한 자동 등록
     */
    AUTO_INJECTED,

    /**
     * 개발자에 의한 명시적 등록
     */
    MANUAL_INJECTED
}
