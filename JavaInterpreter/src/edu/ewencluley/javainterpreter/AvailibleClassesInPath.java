package edu.ewencluley.javainterpreter;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;

import com.google.common.reflect.ClassPath.ClassInfo;

import edu.ewencluley.classloaderinstruments.InstrumentHook;


public class AvailibleClassesInPath {
	
	private static HashMap<String, String> allClasses;
	private static HashMap<String, String> coreJavaLangClasses;
	
	
	public static HashMap<String, String> getClasses(){
		if(allClasses == null){
			allClasses = new HashMap<String, String>();
			coreJavaLangClasses = new HashMap<String, String>();
			Instrumentation inst = InstrumentHook.getInstrumentation();
	        for (Class<?> clazz: inst.getAllLoadedClasses()) {
	        	allClasses.put(clazz.getCanonicalName(), clazz.getSimpleName());
	            if(clazz.getCanonicalName() != null && clazz.getCanonicalName().startsWith("java.lang")){
	            	coreJavaLangClasses.put(clazz.getSimpleName(), clazz.getCanonicalName());
	            }
	        }
		}
		return allClasses;
	}
	
	public static void addClass(Class<?extends Object> clazz){
		if(allClasses == null){
			getClasses();
		}
		allClasses.put(clazz.getCanonicalName(), clazz.getName());
	}
}
