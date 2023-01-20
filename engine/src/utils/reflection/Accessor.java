package utils.reflection;

import java.lang.annotation.Annotation;


public interface Accessor
{
	public Object get();
	public void set(Object value);
	public String getName();
	public Class getType();
	

	
	public <T> T get(Class<T> type);
	public <T extends Annotation> T config(Class<T> annotation);
}