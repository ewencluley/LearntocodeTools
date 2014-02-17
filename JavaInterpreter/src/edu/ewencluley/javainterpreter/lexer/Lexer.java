package edu.ewencluley.javainterpreter.lexer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Lexer {
	public static List<String> tokenize(String inputString){
		ArrayList<String> tokens = new ArrayList<String>();
		Pattern p = Pattern.compile("(.+?)([\\s+](?=(?:(?:[^\"\']*\"){2})*[^\"\']*$)|$)");
		Matcher m = p.matcher(inputString);
		for (int i=0; m.find(); i++){
			String gp1 = m.group(1);
			if(gp1.contains(";")){
				ArrayList<String> subTokens = new ArrayList<String>(Arrays.asList( gp1.split("((?<=;)|(?=;))|((?<=\\{)|(?=\\{))")));
				tokens.addAll(subTokens);
			}else{
				tokens.add(gp1);
			}
		}
		return tokens;
	}
}
/*
 if(m.group(1).contains(";")){
				ArrayList<String> subTokens = new ArrayList<String>(Arrays.asList( m.group(1).split(";")));
				tokens.addAll(subTokens);
			}
 */
