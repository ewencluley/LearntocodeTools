package edu.ewencluley.javainterpreter.lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.reflect.ClassPath;

import edu.ewencluley.javainterpreter.AvailibleClassesInPath;
import edu.ewencluley.javainterpreter.Workbench;
import edu.ewencluley.javainterpreter.syntax.Construct;
import edu.ewencluley.javainterpreter.syntax.Line;
import edu.ewencluley.javainterpreter.syntax.MethodCall;


public class LineConstructor {

	public static List<Line> splitLines(List<String> tokens){
		ArrayList<Line> lines = new ArrayList<Line>();
		Line currentLine = new Line();
		int parenthisisOpen = 0;
		boolean newLine = true;
		PeekingIterator<String> tokenIterator = Iterators.peekingIterator(tokens.iterator());
		while(tokenIterator.hasNext()){
			String token = tokenIterator.next();
			if(newLine){
				if(token.equals("import")){
					currentLine.add(token);
					currentLine.add(importStatement(tokenIterator));
				}else if(token.startsWith("class")){
					currentLine = new Construct();
				}else if(token.matches("(\\w+(.?))*(\\w)+\\([\\w\\\"\\)]*")){
					String methodName = token.substring(token.lastIndexOf(".", token.indexOf("("))+1, token.indexOf("("));
					String objectName = token.substring(0, token.indexOf("."));
					Object object = Workbench.variableByName(objectName);
					//currentLine = new MethodCall();
					System.out.println(methodName); 
				}else{
					String fqName = Workbench.getQualifiedClassName(token);
					if(fqName != null){
						String varName = tokenIterator.next();
						Workbench.newVariable(varName, null);
						if(tokenIterator.peek().equals("=")){
							tokenIterator.next();
							expression(tokenIterator);
						}
					}else{
						currentLine = new Line();
					}
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
	
	private static String importStatement(PeekingIterator<String> tokens){
		String packageName = tokens.next();
		if(packageName.endsWith("*")){
			ClassPath classpath;
			try {
				classpath = ClassPath.from(ClassLoader.getSystemClassLoader());
				for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(packageName.replace(".*", ""))) {
					loadClass(classInfo.getName());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			loadClass(packageName);
		}
		return packageName;
	}
	
	private static void loadClass(String className){
		try {
			Class<? extends Object> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
			AvailibleClassesInPath.addClass(clazz);
			System.err.println("class loaded for "+ className);
		} catch (ClassNotFoundException e) {
			System.err.println("no class exists for "+ className);
			e.printStackTrace();
		}
		
	}
	
	
	private static boolean expression(PeekingIterator<String> tokens){
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
