package backend.data;

import com.badlogic.gdx.utils.Array;

import java.lang.annotation.Annotation;

abstract public class ClassRegistry
{

    public static final ClassRegistry none = new ClassRegistry()
    {

        @Override
        public <T> Array<Class<? extends T>> getSubTypesOf(Class<T> type) {
            return new Array<Class<? extends T>>();
        }

        @Override
        public Array<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
            return new Array<Class<?>>();
        }

        @Override
        public Array<Class<?>> getClasses() {
            return new Array<Class<?>>();
        }
    };

    private static ClassRegistry instance = none;

    public static ClassRegistry getInstance() {
        return instance;
    }

    abstract public <T> Array<Class<? extends T>> getSubTypesOf(Class<T> type);

    abstract public Array<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);

    abstract public Array<Class<?>> getClasses();

}