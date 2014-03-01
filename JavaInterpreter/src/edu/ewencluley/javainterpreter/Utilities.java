package edu.ewencluley.javainterpreter;

import java.util.HashMap;
import java.util.Set;

public class Utilities {
	
	public static boolean isValidType(String token){
		String tok = type(token);
		return (tok != null);
	}
	
	public static String type(String token){
		if(token.endsWith("[]")){
			token = token.substring(0, token.lastIndexOf("[")-1);
			if(token.endsWith("[]")){
				type(token);
			}
		}
		switch(token){
		case "byte":
		case "short":
		case "int":
		case "long":
		case "boolean":
		case "double":
		case "char":
		case "float":
		case "void": //not really a type but sort of is
			return token;
		default:
			if(getQualifiedClassName(token) != null
			&& getQualifiedClassName(token).endsWith(token)){
				return getQualifiedClassName(token);
			}
			break;
		}
		//TODO line number;
		return null;
	}
	
	public static String getQualifiedClassName(String name){
		HashMap<String,String> classes = AvailibleClassesInPath.getClasses();
		Set<String> fqClasses = classes.keySet();
		for(String fqClass:fqClasses){
			if(fqClass != null && fqClass.endsWith("."+name)){
				return classes.get(fqClass);
			}
		}
		return null;
	}
	
}
