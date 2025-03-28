package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodInvocation implements Serializable {

	@Serial
	private static final long serialVersionUID = 20240718L;

	// Method is not serializable 😢
	public final transient Method method;
	public final String methodName;
	public final Object[] arguments;
	public final boolean isMaintenance;
	public final boolean isOptional;

	public MethodInvocation(Method method, Object[] arguments, boolean isMaintenance) {
		this.method = method;
		this.methodName = method.getName(); // Store method name for serialization
		this.arguments = arguments;
		this.isMaintenance = isMaintenance;
		this.isOptional = method.isAnnotationPresent(NetOptional.class);
	}

	/**
	 * Perform this method invocation against the target object.
	 */
	public Object against(Object target) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		// assume this method invocation was instantiated via serialization
		if(method == null) {
			final Class<?>[] argTypes;
			if(arguments != null)
				argTypes = Arrays.stream(arguments).map(Object::getClass).toArray(Class<?>[]::new);
			else
				argTypes = new Class<?>[0];
			return target.getClass().getMethod(methodName, argTypes).invoke(target, arguments);
		} else {
			return method.invoke(target, arguments);
		}
	}

}
