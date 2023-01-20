package backend.annotations;

import backend.serialization.AnnotationBasedSerializer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD , TYPE})
public @interface Storable
{

    /**
     * Define name in serialized format. Highly recommanded for safe refactoring.
     */
    String value() default "";

    /**
     * Define whether a {@link AnnotationBasedSerializer} should be created for this type.
     * Default is false. That is all non transient accessible fields are persisted.
     * When true, only fields annotated with {@link Storable} will be persisted.
     * Changing this option prevent to mark all other field transient.
     */
    boolean auto() default false;

}
