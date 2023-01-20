package backend.serialization;

import backend.annotations.Storable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import utils.reflection.ReflectionHelper;

import java.lang.reflect.Field;

public class AnnotationBasedSerializer implements Json.Serializer
{

    private Class type;

    public AnnotationBasedSerializer(Class type) {
        super();
        this.type = type;
    }

    @Override
    public void write(Json json , Object object , Class knownType) {
        json.writeObjectStart();
        for (Field field : type.getFields()) {
            Storable storable = field.getAnnotation(Storable.class);
            if (storable != null) {
                json.writeField(object , field.getName());
            }
        }
        json.writeObjectEnd();
    }

    @Override
    public Object read(Json json , JsonValue jsonData , Class type) {
        Object object = ReflectionHelper.newInstance(type);
        for (Field field : type.getFields()) {
            Storable storable = field.getAnnotation(Storable.class);
            if (storable != null) {
                json.readField(object , field.getName() , jsonData);
            }
        }
        return object;
    }

}