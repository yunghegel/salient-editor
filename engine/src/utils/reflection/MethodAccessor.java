package utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;



public class MethodAccessor extends AccessorBase
{
	private Object object;
	private String name;
	private Method getter;
	private Method setter;
	
	
	public MethodAccessor(Object object, String name, String getter, String setter) {
		super();
		this.object = object;
		this.name = name;
		this.getter = ReflectionHelper.method(object.getClass(), getter);
		this.setter = ReflectionHelper.method(object.getClass(), setter, this.getter.getReturnType());
	}
	public MethodAccessor(Object object, String name, Method getter, Method setter) {
		super();
		this.object = object;
		this.name = name;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public Object get() {
		return ReflectionHelper.invoke(object, getter);
	}

	@Override
	public void set(Object value) {
		ReflectionHelper.invoke(object, setter, value);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class getType() {
		return getter.getReturnType();
	}
	
	@Override
	public <T extends Annotation> T config(Class<T> annotation) {
		T a = setter.getAnnotation(annotation);
		if(a == null){
			a = getter.getAnnotation(annotation);
		}
		return a;
	}
	
}