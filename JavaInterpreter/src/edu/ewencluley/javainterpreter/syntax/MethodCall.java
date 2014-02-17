package edu.ewencluley.javainterpreter.syntax;
import java.lang.reflect.*;
import java.util.ArrayList;

public class MethodCall extends Line {

	String methodName;
	Object object;
	Class cls;
	Class[] argTypes = {};
	boolean methodIsStatic;
	ArrayList arguments;
	ArrayList argumentTypes;
	
	public MethodCall(String methodName, Object object, Class cls, ArrayList arguments, Class[] argumentTypes){
		this.methodName = methodName;
		this.object = object;
		this.cls = cls;
		this.argTypes = argumentTypes;
		this.arguments = arguments;
	}
	
	public Object interpret(){
		System.out.println("Start interpret method:"+ methodName);
		Method method;
		try {
			method = cls.getDeclaredMethod(methodName, argTypes);
			Object result = method.invoke(object, arguments);
			System.out.println("Finished interpret method:"+ methodName);
			return result;
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
}
