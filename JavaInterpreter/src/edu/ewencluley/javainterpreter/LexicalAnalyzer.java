package edu.ewencluley.javainterpreter;

import java.io.*;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

/**
 * The Lexical Analyzer
 * 
 * Controls the reading of the input file and creates tokens for specific patters of characters as defined by the language dl0
 * @author 52594
 * @version 1.0 (27/10/10)
 *
 */
public class LexicalAnalyzer 
{
	private Parser parser; //a parser object that reads in a stream of characters from a file.
	private char currentChar; // the current character being processed
	private LinkedList<Character> backtrackedChars= new LinkedList<Character>();

	/**
	 * The Lexical Analyzer's constructor.  This takes in a File and will and prepares a parser object to read it
	 * @param f File the file to be analyzed.
	 */
	public LexicalAnalyzer(File f)
	{
		parser = new Parser(f); 

		currentChar = getNextChar();
	}
	
	public LexicalAnalyzer(String s)
	{
		parser = new Parser(s); 

		currentChar = getNextChar();
	}

	/**
	 * Gets the next character from the parser
	 * @return the next character in the file being read.
	 */
	public char getNextChar()
	{
		char currentChar;
		if(!backtrackedChars.isEmpty()){
			currentChar = backtrackedChars.removeFirst();
		}
		try{
			currentChar = parser.readNextChar();
		} catch (IOException e){currentChar = '¤';}

		return currentChar;

	}

	/**
	 * Analyzes the character/characters and generates a token to represent it.
	 * @return the current token found from a sequence of characters
	 */
	public Token getToken()
	{
		while(Character.isWhitespace(currentChar)){
			currentChar = getNextChar(); //if whitespace, skip over the whitespace
		}
		
		//each of the following identify single character operators such as '=', '*', etc.
		if(currentChar == '\b'){ //if the character is an "!" (returned from the parser once it reaches the end of file)
			currentChar = getNextChar();
			return new Token("eof","EndOfFile"); // return an end of file token.
		}else if(currentChar=='+'){
			currentChar = getNextChar();
			if(currentChar == '+'){
				currentChar = getNextChar();
				return new Token("++", "IncrementOp");
			}else{
				backtrackedChars.add(currentChar);
				return new Token("+", "AddOp");
			}
		}else if(currentChar=='-'){
			currentChar = getNextChar();
			if(currentChar == '-'){
				currentChar = getNextChar();
				return new Token("--", "DecrementOp");
			}else{
				backtrackedChars.add(currentChar);
				return new Token("-", "AddOp");
			}
		}else if(currentChar=='*'){
			currentChar = getNextChar();
			return new Token("*", "MultOp");
		}else if(currentChar=='/'){
			currentChar = getNextChar();
			return new Token("/", "MultOp");
		}else if(currentChar=='%'){
			currentChar = getNextChar();
			return new Token("%", "ModulusOp");
		}else if(currentChar=='{'){
			currentChar = getNextChar();
			return new Token("{", "BlockOpen");
		}else if(currentChar=='}'){
			currentChar = getNextChar();
			return new Token("}", "BlockClose");
		}else if(currentChar==','){
			currentChar = getNextChar();
			return new Token(",", "Comma");
		}else if(currentChar==';'){
			currentChar = getNextChar();
			return new Token(";", "Semi-Colon");
		}else if(currentChar=='('){
			currentChar = getNextChar();
			return new Token("(", "OpenParenthesis");
		}else if(currentChar==')'){
			currentChar = getNextChar();
			return new Token(")", "CloseParenthesis");
		//the next section checks if characters form words, i.e. are letters. The characters must be lowercase as defined in the language dl0 
		}else if(currentChar=='~'){
			currentChar = getNextChar();
			return new Token("~", "BitwiseComplement");
		}else if(currentChar=='!'){
			currentChar = getNextChar();
			if(currentChar == '='){
				currentChar = getNextChar();
				return new Token("!=", "BooleanNotEqual");
			}else{
				backtrackedChars.add(currentChar);
				return new Token("!", "BooleanNot");
			}
			
		}else if(currentChar=='|'){
			currentChar = getNextChar();
			if(currentChar == '|'){
				currentChar = getNextChar();
				return new Token("||", "BooleanOr");
			}else{
				backtrackedChars.add(currentChar);
				return new Token("|", "BitwiseOr");
			}
		}else if(currentChar=='&'){
			currentChar = getNextChar();
			if(currentChar == '&'){
				currentChar = getNextChar();
				return new Token("&&", "BooleanAnd");
			}else{
				backtrackedChars.add(currentChar);
				return new Token("&", "BitwiseAnd");
			}
		}else if(currentChar=='='){
			currentChar = getNextChar();
			if(currentChar == '='){
				currentChar = getNextChar();
				return new Token("==", "BooleanEquality");
			}else{
				backtrackedChars.add(currentChar);
				return new Token("=", "AssignmentOperator");
			}
		}else if(currentChar == '^'){
			currentChar = getNextChar();
			return new Token("^", "BitwiseXOR");
		}else if(currentChar=='<'){
			currentChar = getNextChar();
			if(currentChar == '<'){
				currentChar = getNextChar();
				return new Token("<<", "BitwiseLeftShift");
			}else if(currentChar == '='){
				return new Token("<", "BooleanLessThanOrEqual");
			}else{
				backtrackedChars.add(currentChar);
				return new Token("<", "BooleanLessThan");
			}
		}else if(currentChar=='>'){
			currentChar = getNextChar();
			if(currentChar == '>'){
				currentChar = getNextChar();
				if(currentChar == '>'){
					currentChar = getNextChar();
					return new Token(">>>", "BitwiseUnsignedRightShift");
				}else{
					backtrackedChars.add(currentChar);
					return new Token(">>", "BitwiseRightShift");
				}
			}else if(currentChar == '='){
				return new Token(">=", "BooleanGreaterThanOrEqual");
			}else{
				backtrackedChars.add(currentChar);
				return new Token(">", "BooleanGreaterThan");
			}
		}else if(currentChar == '\"'){
			String word = new String(); //new empty string
			currentChar = getNextChar();
			while(currentChar != '\"'){ //while the next character is not a quote.
				word += currentChar;
				currentChar =getNextChar();
			}
			return new Token(word, "StringLiteral");
		}else if(currentChar == '\''){
			String word = new String(); //new empty string
			currentChar = getNextChar();
			while(currentChar != '\''){ //while the next character is not a quote.
				word += currentChar;
				currentChar =getNextChar();
			}
			currentChar =getNextChar();
			if(word.length() == 1){
				return new Token(word, "CharLiteral");
			}else{
				return new Token(word, "StringLiteral");
			}
		}else if(Character.isAlphabetic(currentChar)){
			String word = new String(""+currentChar); //puts the first character into the "word"
			currentChar = getNextChar();
			while(Character.isAlphabetic(currentChar) || Character.isDigit(currentChar) || currentChar == '\"'){ //while the next character is a lowercase Letter of Digit it is added to the word. 
				//If it is any other character, the loop will end
				word += currentChar;
				currentChar =getNextChar();
			}
			//these cases are if the word found matches one of the system reserved words (int or print)
			if(word.equals("instanceof")){return new Token("instanceof", "BooleanInstanceOf");}
			else if(word.equals("null")){return new Token("null", "NullLiteral");}
			else if(word.equals("true") || word.equals("false")){return new Token(word, "BooleanLiteral");}
			else if(Utilities.isValidType(word)){return new Token(word, "Type");}
			else{ //if the word is not a system reserved word then it is returned as an identifier.
				return new Token(word, "Identifier");
			}
		//the last section checks if the characters form a constant.
		}else if(Character.isDigit(currentChar)){
			String word = new String(""+currentChar); //adds first digit to a "word"
			currentChar = getNextChar();
			while(Character.isDigit(currentChar) || currentChar == '.'){//while digits or points follow it directly, add these till the word
				word += currentChar;
				currentChar =getNextChar();
			}
			if((currentChar == 'f' || currentChar == 'F') && StringUtils.countMatches(word, ".") == 1){
				currentChar = getNextChar();
				return new Token(word + currentChar, "FloatLiteral");
			}else if(StringUtils.countMatches(word, ".") == 1){
				if((currentChar == 'd' || currentChar == 'D')){
					currentChar =getNextChar();
				}
				return new Token(word + currentChar, "DoubleLiteral");
			}else if((currentChar == 'l' || currentChar == 'L') && StringUtils.countMatches(word, ".") == 0){
				currentChar =getNextChar();
				return new Token(word + currentChar, "LongLiteral");
			}else{
				return new Token(word + currentChar, "IntegerLiteral");
			}
		}else{ //if none of the above conditions are met, the character found is not recognized as part of of the dl0 grammar
			char lastChar=currentChar;
			currentChar = getNextChar();
			return new Token("error on character:"+lastChar,"Error"); //an error token is returned
		}
	}
}
