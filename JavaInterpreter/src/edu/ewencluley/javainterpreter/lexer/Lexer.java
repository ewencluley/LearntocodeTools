package edu.ewencluley.javainterpreter.lexer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Lexer {
	public static List<String> tokenize(String inputString){
		ArrayList<String> tokens = new ArrayList<String>();
		Pattern p = Pattern.compile("(.+?)(\\s+(?=(?:(?:[^\"\']*\"){2})*[^\"\']*$)|$)");
		Matcher m = p.matcher(inputString);
		for (int i=0; m.find(); i++)
		    tokens.add(m.group(1).replace("\"", ""));
		return tokens;
	}
}
