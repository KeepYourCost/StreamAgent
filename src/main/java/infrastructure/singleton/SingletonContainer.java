package infrastructure.singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// package에서만 접근가능
class SingletonContainer {
    protected static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();
}
