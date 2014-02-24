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
import edu.ewencluley.javainterpreter.exceptions.InvalidTypeException;
import edu.ewencluley.javainterpreter.syntax.Construct;
import edu.ewencluley.javainterpreter.syntax.Line;
import edu.ewencluley.javainterpreter.syntax.MethodCall;


public class LineConstructor {

	/*public static List<Line> splitLines(List<String> tokens){
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
	}*/
	
	private static String importStatement(List<String> tokens){
		String packageName = tokens.get(1);
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
	
	
	private static boolean expression(List<String> tokens) throws InvalidTypeException{
		if(booleanLiteral(tokens.get(0))){
			removeTokens(tokens, 1);
			return true;
		}
		else if(integerLiteral(tokens.get(0))){
			removeTokens(tokens, 1);
			return true;
		}else if(floatLiteral(tokens.get(0))){
			removeTokens(tokens, 1);
			return true;
		}else if(doubleLiteral(tokens.get(0))){
			removeTokens(tokens, 1);
			return true;
		}else if(thisLiteral(tokens.get(0))){
			removeTokens(tokens, 1);
			return true;
		}else if(identifier(tokens.get(0))){
			removeTokens(tokens, 1);
			return true;
		}else if(expression(tokens)){
			if(tokens.get(0).equals("&&") || tokens.get(0).equals("||") 
					|| tokens.get(0).equals("+") || tokens.get(0).equals("-") || tokens.get(0).equals("*") || tokens.get(0).equals("/") 
					|| tokens.get(0).equals("<") || tokens.get(0).equals(">") || tokens.get(0).equals(">=") || tokens.get(0).equals("<=")){
				List<String> moddedList = tokens;
				removeTokens(moddedList, 1);
				if(expression(moddedList)){
					tokens = moddedList;
				}
			}
		}
		return false;
				
	}
	
	private static boolean thisLiteral(String token) {
		if(token.equals("this")){
			return true;
		}
		return false;
	}

	private static boolean booleanLiteral(String token) {
		if(token.equals("true") || token.equals("false")){
			return true;
		}
		return false;
	}
	
	private static boolean integerLiteral(String token) {
		if(token.matches("\\d+")){
			return true;
		}
		return false;
	}
	private static boolean floatLiteral(String token) {
		if(token.matches("\\d+(\\.\\d+)?f")){
			return true;
		}
		return false;
	}
	private static boolean doubleLiteral(String token) {
		if(token.matches("\\d+(\\.\\d+)?")){
			return true;
		}
		return false;
	}

	public static void analyseSyntax(List<String> tokens) throws InvalidTypeException{
		if(varDeclaration(tokens)){
			System.out.println("found var declaration");
			removeTokens(tokens, 3);
		}
		while(expression(tokens)){
			System.out.println("found expression");
			//removeTokens(tokens, 4);
		}
	}
	
	private static void removeTokens(List<String> tokens, int howMany){
		for(int i=0; i< howMany; i++){
			tokens.remove(0);
		}
	}
	
	
	private static boolean varDeclaration(List<String> tokens) throws InvalidTypeException{
		if(type(tokens.get(0)) != null){
			if(identifier(tokens.get(1))){
				if(tokens.get(2).equals(";")){
					return true;
				}
			}
		}
		return false;
	}
	
	private static String type(String token) throws InvalidTypeException{
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
			return token;
		default:
			if(Workbench.getQualifiedClassName(token) != null
			&& Workbench.getQualifiedClassName(token).endsWith(token)){
				return Workbench.getQualifiedClassName(token);
			}
			break;
		}
		//TODO line number;
		return null;
	}
	
	private static boolean identifier(String token){
		if(token.matches("[A-Za-z0-9]+")){
			return true;
		}
		return false;
	}
	
	private boolean existingVariableName(String token){
		if(Workbench.variableExists(token)){
			return true;
		}
		return false;
	}
}
