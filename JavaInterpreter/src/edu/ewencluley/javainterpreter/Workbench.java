package edu.ewencluley.javainterpreter;

import java.util.HashMap;
import java.util.Set;

import com.google.common.reflect.ClassPath.ClassInfo;

public class Workbench {
	private static HashMap<String, Object> existingVariables = new HashMap<String, Object>();
	
	public static boolean variableExists(String varName){
		return existingVariables.containsKey(varName);
	}
	
	public static Object variableByName(String varName){
		return existingVariables.get(varName);
	}
	
	public static void newVariable(String name, Object obj){
		existingVariables.put(name, obj);
	}
	
	public static String getQualifiedClassName(String name){
		HashMap<String,String> classes = AvailibleClassesInPath.getClasses();
		Set<String> fqClasses = classes.keySet();
		for(String fqClass:fqClasses){
			if(fqClass != null && fqClass.endsWith(name)){
				return classes.get(fqClass);
			}
		}
		return null;
	}	
}
