package utils.reflection;

import java.lang.annotation.Annotation;



abstract public class AccessorBase implements Accessor
{

	@Override
	public <T> T get(Class<T> type) {
		Object value = get();
		// TODO use Float instead of float if not it will not be assignable !
		if(value != null && type.isAssignableFrom(value.getClass())){
			return (T)get();
		}
		return null;
	}
	
	@Override
	public <T extends Annotation> T config(Class<T> annotation) {
		return null;
	}
	


}
