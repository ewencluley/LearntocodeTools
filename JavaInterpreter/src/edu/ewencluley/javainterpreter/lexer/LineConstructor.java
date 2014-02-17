package edu.ewencluley.javainterpreter.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import edu.ewencluley.javainterpreter.AvailibleClassesInPath;
import edu.ewencluley.javainterpreter.Workbench;
import edu.ewencluley.javainterpreter.syntax.Construct;
import edu.ewencluley.javainterpreter.syntax.Line;


public class LineConstructor {

	public static List<Line> splitLines(List<String> tokens){
		ArrayList<Line> lines = new ArrayList<Line>();
		Line currentLine = new Line();
		int parenthisisOpen = 0;
		boolean newLine = true;
		for(String token:tokens){
			if(newLine){
				
				if(token.startsWith("for")){
					currentLine = new Construct();
				}else{
					currentLine = new Line();
				}
				newLine = false;
			}
			parenthisisOpen += StringUtils.countMatches(token, "(");
			parenthisisOpen -= StringUtils.countMatches(token, ")");
			
			if(!token.equals("\n")){
				currentLine.add(token);
			}
			if((token.endsWith(";") || token.endsWith("{") || token.endsWith("}"))&& parenthisisOpen == 0){ //if end of line 
				lines.add(currentLine);
				newLine = true;
			}
		}
		return lines;
	}
	
	private boolean importStatement(List<String> tokens){
		if(tokens.get(0).equals("import")){
			String packageName = tokens.get(1);
			if(packageName.endsWith("*")){
				Reflections reflections = new Reflections(packageName.replace(".*", ""));
				Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
				for(Class<?extends Object> clazz: allClasses){
					loadClass(clazz.getCanonicalName());
				}
			}else{
				loadClass(packageName);
			}
			return true;
		}
		return false;
	}
	
	private void loadClass(String className){
		try {
			Class<? extends Object> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
			AvailibleClassesInPath.addClass(clazz);
			System.err.println("class loaded for "+ className);
		} catch (ClassNotFoundException e) {
			System.err.println("no class exists for "+ className);
			e.printStackTrace();
		}
		
	}
	
	
	private boolean expression(List<String> tokens){
		//if(className(tokens.get(0))){
			
		//}
		return false;
				
	}
	
	/**
	 * Gets the fully qualified class name if the class is visible, else it return null.
	 * @param token the name of the class to find
	 * @return the fully qualified name of the given class
	 */
	private String className(String token){
		if(token.matches("[A-Za-z]\\w*")){
			return "";//AvailibleClassesInPath.;
		}
		return "";
	}
	
	private boolean variableName(String token){
		if(token.matches("[A-Za-z0-9]+")){
			Workbench.newVariable(token, null);
			return true;
		}
		return false;
	}
	
	private boolean existingVariableName(String token){
		if(token.matches("[A-Za-z0-9]+") && Workbench.variableExists(token)){
			return true;
		}
		return false;
	}
}
