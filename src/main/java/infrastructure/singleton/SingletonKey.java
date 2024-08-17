package infrastructure.singleton;

import java.util.Objects;

class SingletonKey {
    protected Class<?> aClass;
    protected InjectionStrategy injectionStrategy;

    protected SingletonKey(
            Class<?> aClass,
            InjectionStrategy injectionStrategy
    ) {
        this.aClass = aClass;
        this.injectionStrategy = injectionStrategy;
    }

    // 비교시에는 Class 값만 비교하도록 한다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingletonKey key = (SingletonKey) o;
        return Objects.equals(aClass, key.aClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aClass);
    }
}
