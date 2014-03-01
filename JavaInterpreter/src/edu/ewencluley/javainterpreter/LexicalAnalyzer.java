package edu.ewencluley.javainterpreter;

import java.io.*;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

import edu.ewencluley.javainterpreter.exceptions.InvalidCharacterException;
import edu.ewencluley.javainterpreter.lexer.tokens.ArithmeticOperationToken;
import edu.ewencluley.javainterpreter.lexer.tokens.AssignmentToken;
import edu.ewencluley.javainterpreter.lexer.tokens.BitwiseOperationToken;
import edu.ewencluley.javainterpreter.lexer.tokens.BooleanOperationToken;
import edu.ewencluley.javainterpreter.lexer.tokens.ConstructToken;
import edu.ewencluley.javainterpreter.lexer.tokens.EndOfInputToken;
import edu.ewencluley.javainterpreter.lexer.tokens.IdentifierToken;
import edu.ewencluley.javainterpreter.lexer.tokens.LiteralToken;
import edu.ewencluley.javainterpreter.lexer.tokens.TokenTypes;
import edu.ewencluley.javainterpreter.lexer.tokens.TokenTypes.LiteralTypes;
import edu.ewencluley.javainterpreter.lexer.tokens.Token;
import edu.ewencluley.javainterpreter.lexer.tokens.TypeToken;

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
	public Token getToken() throws InvalidCharacterException
	{
		while(Character.isWhitespace(currentChar)){
			currentChar = getNextChar(); //if whitespace, skip over the whitespace
		}
		
		//each of the following identify single character operators such as '=', '*', etc.
		if(currentChar == '\b'){ //if the character is an "!" (returned from the parser once it reaches the end of file)
			currentChar = getNextChar();
			return new EndOfInputToken("EndOfFile"); // return an end of file token.
		}else if(currentChar=='+'){
			currentChar = getNextChar();
			if(currentChar == '+'){
				currentChar = getNextChar();
				return new ArithmeticOperationToken("++", TokenTypes.ArithmeticOperationTypes.INC_DEC);
			}else{
				backtrackedChars.add(currentChar);
				return new ArithmeticOperationToken("+", TokenTypes.ArithmeticOperationTypes.ADDITION);
			}
		}else if(currentChar=='-'){
			currentChar = getNextChar();
			if(currentChar == '-'){
				currentChar = getNextChar();
				return new ArithmeticOperationToken("--", TokenTypes.ArithmeticOperationTypes.INC_DEC);
			}else{
				backtrackedChars.add(currentChar);
				return new ArithmeticOperationToken("-", TokenTypes.ArithmeticOperationTypes.ADDITION);
			}
		}else if(currentChar=='*'){
			currentChar = getNextChar();
			return new ArithmeticOperationToken("*", TokenTypes.ArithmeticOperationTypes.MULTIPLICATION);
		}else if(currentChar=='/'){
			currentChar = getNextChar();
			return new ArithmeticOperationToken("/", TokenTypes.ArithmeticOperationTypes.MULTIPLICATION);
		}else if(currentChar=='%'){
			currentChar = getNextChar();
			return new ArithmeticOperationToken("%", TokenTypes.ArithmeticOperationTypes.MODULUS);
		}else if(currentChar=='{'){
			currentChar = getNextChar();
			return new ConstructToken("{", TokenTypes.ConstructTypes.BLOCK_OPEN);
		}else if(currentChar=='}'){
			currentChar = getNextChar();
			return new ConstructToken("}", TokenTypes.ConstructTypes.BLOCK_CLOSE);
		}else if(currentChar==','){
			currentChar = getNextChar();
			return new ConstructToken(",", TokenTypes.ConstructTypes.COMMA);
		}else if(currentChar==';'){
			currentChar = getNextChar();
			return new ConstructToken(";", TokenTypes.ConstructTypes.SEMI_COLON);
		}else if(currentChar=='('){
			currentChar = getNextChar();
			return new ConstructToken("(", TokenTypes.ConstructTypes.PARENTHISIS);
		}else if(currentChar==')'){
			currentChar = getNextChar();
			return new ConstructToken(")", TokenTypes.ConstructTypes.PARENTHISIS);
		}else if(currentChar=='['){
			currentChar = getNextChar();
			return new ConstructToken("[", TokenTypes.ConstructTypes.SQ_PARENTHISIS);
		}else if(currentChar==']'){
			currentChar = getNextChar();
			return new ConstructToken("]", TokenTypes.ConstructTypes.SQ_PARENTHISIS);
		}else if(currentChar=='~'){
			currentChar = getNextChar();
			return new BitwiseOperationToken("~", TokenTypes.BitwiseOperationTypes.COMPLEMENT);
		}else if(currentChar=='!'){
			currentChar = getNextChar();
			if(currentChar == '='){
				currentChar = getNextChar();
				return new BooleanOperationToken("!=", TokenTypes.BooleanOperationTypes.NOT_EQUAL);
			}else{
				backtrackedChars.add(currentChar);
				return new BooleanOperationToken("!", TokenTypes.BooleanOperationTypes.NOT);
			}
		}else if(currentChar=='|'){
			currentChar = getNextChar();
			if(currentChar == '|'){
				currentChar = getNextChar();
				return new BooleanOperationToken("||", TokenTypes.BooleanOperationTypes.OR);
			}else{
				backtrackedChars.add(currentChar);
				return new BitwiseOperationToken("|", TokenTypes.BitwiseOperationTypes.OR);
			}
		}else if(currentChar=='&'){
			currentChar = getNextChar();
			if(currentChar == '&'){
				currentChar = getNextChar();
				return new BooleanOperationToken("&&", TokenTypes.BooleanOperationTypes.AND);
			}else{
				backtrackedChars.add(currentChar);
				return new BitwiseOperationToken("&", TokenTypes.BitwiseOperationTypes.AND);
			}
		}else if(currentChar=='='){
			currentChar = getNextChar();
			if(currentChar == '='){
				currentChar = getNextChar();
				return new BooleanOperationToken("!=", TokenTypes.BooleanOperationTypes.EQUAL);
			}else{
				backtrackedChars.add(currentChar);
				return new AssignmentToken("=");
			}
		}else if(currentChar == '^'){
			currentChar = getNextChar();
			return new BitwiseOperationToken("^", TokenTypes.BitwiseOperationTypes.XOR);
		}else if(currentChar=='<'){
			currentChar = getNextChar();
			if(currentChar == '<'){
				currentChar = getNextChar();
				return new BitwiseOperationToken("<<", TokenTypes.BitwiseOperationTypes.LEFT_SHIFT_SIGNED);
			}else if(currentChar == '='){
				currentChar = getNextChar();
				return new BooleanOperationToken("<=", TokenTypes.BooleanOperationTypes.LESS_EQUAL);
			}else{
				backtrackedChars.add(currentChar);
				return new BooleanOperationToken("<", TokenTypes.BooleanOperationTypes.LESS);
			}
		}else if(currentChar=='>'){
			currentChar = getNextChar();
			if(currentChar == '>'){
				currentChar = getNextChar();
				if(currentChar == '>'){
					currentChar = getNextChar();
					return new BitwiseOperationToken(">>>", TokenTypes.BitwiseOperationTypes.RIGHT_SHIFT_UNSIGNED);
				}else{
					backtrackedChars.add(currentChar);
					return new BitwiseOperationToken(">>", TokenTypes.BitwiseOperationTypes.RIGHT_SHIFT_SIGNED);
				}
			}else if(currentChar == '='){
				currentChar = getNextChar();
				return new BooleanOperationToken(">=", TokenTypes.BooleanOperationTypes.GREATER_EQUAL);
			}else{
				backtrackedChars.add(currentChar);
				return new BooleanOperationToken(">", TokenTypes.BooleanOperationTypes.GREATER);
			}
		}else if(currentChar == '\"'){
			String word = new String(); //new empty string
			currentChar = getNextChar();
			while(currentChar != '\"'){ //while the next character is not a quote.
				word += currentChar;
				currentChar =getNextChar();
			}
			return new LiteralToken(word, TokenTypes.LiteralTypes.STRING);
		}else if(currentChar == '\''){
			String word = new String(); //new empty string
			currentChar = getNextChar();
			while(currentChar != '\''){ //while the next character is not a quote.
				word += currentChar;
				currentChar =getNextChar();
			}
			currentChar =getNextChar();
			if(word.length() == 1){
				return new LiteralToken(word, TokenTypes.LiteralTypes.CHAR);
			}else{
				return new LiteralToken(word, TokenTypes.LiteralTypes.STRING);
			}
		}else if(Character.isAlphabetic(currentChar)){
			String word = new String(""+currentChar); //puts the first character into the "word"
			currentChar = getNextChar();
			while(Character.isJavaIdentifierPart(currentChar) || currentChar == '[' || currentChar == ']'){ //while the next character is a lowercase Letter of Digit it is added to the word. 
				//If it is any other character, the loop will end
				word += currentChar;
				currentChar =getNextChar();
			}
			if(word.equals("instanceof")){return new BooleanOperationToken("instanceof", TokenTypes.BooleanOperationTypes.INSTANCEOF);}
			else if(word.equals("class")){return new ConstructToken("class", TokenTypes.ConstructTypes.CLASS_DEF);}
			else if(word.equals("extends")){return new ConstructToken("extends", TokenTypes.ConstructTypes.EXTENDS);}
			else if(word.equals("implements")){return new ConstructToken("implements", TokenTypes.ConstructTypes.IMPLEMENTS);}
			else if(word.equals("public")){return new ConstructToken("public", TokenTypes.ConstructTypes.ACCESS_MODIFIER);}
			else if(word.equals("private")){return new ConstructToken("private", TokenTypes.ConstructTypes.ACCESS_MODIFIER);}
			else if(word.equals("protected")){return new ConstructToken("protected", TokenTypes.ConstructTypes.ACCESS_MODIFIER);}
			else if(word.equals("default")){return new ConstructToken("default", TokenTypes.ConstructTypes.ACCESS_MODIFIER);}
			else if(word.equals("return")){return new ConstructToken("return", TokenTypes.ConstructTypes.RETURN);}
			else if(word.equals("for")){return new ConstructToken("for", TokenTypes.ConstructTypes.FOR);}
			else if(word.equals("if")){return new ConstructToken("if", TokenTypes.ConstructTypes.IF);}
			else if(word.equals("else")){return new ConstructToken("else", TokenTypes.ConstructTypes.ELSE);}
			else if(word.equals("static")){return new ConstructToken("static", TokenTypes.ConstructTypes.STATIC);}
			else if(word.equals("abstract")){return new ConstructToken("abstract", TokenTypes.ConstructTypes.ABSTRACT);}
			else if(word.equals("new")){return new ConstructToken("new", TokenTypes.ConstructTypes.NEW);}
			else if(word.equals("null")){return new LiteralToken(word, TokenTypes.LiteralTypes.NULL);}
			else if(word.equals("true") || word.equals("false")){return new LiteralToken(word, TokenTypes.LiteralTypes.BOOLEAN);}
			else if(Utilities.isValidType(word)){return new TypeToken(word);}
			else{ //if the word is not a system reserved word then it is returned as an identifier.
				return new IdentifierToken(word);
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
				return new LiteralToken(word, TokenTypes.LiteralTypes.FLOAT);
			}else if(StringUtils.countMatches(word, ".") == 1){
				if((currentChar == 'd' || currentChar == 'D')){
					currentChar =getNextChar();
				}
				return new LiteralToken(word, TokenTypes.LiteralTypes.DOUBLE);
			}else if((currentChar == 'l' || currentChar == 'L') && StringUtils.countMatches(word, ".") == 0){
				currentChar =getNextChar();
				return new LiteralToken(word, TokenTypes.LiteralTypes.LONG);
			}else{
				return new LiteralToken(word, TokenTypes.LiteralTypes.INTEGER);
			}
		}else if(currentChar == '.'){
			currentChar =getNextChar();
			return new ConstructToken(".", TokenTypes.ConstructTypes.DOT);
		}else{ //if none of the above conditions are met, the character found is not recognized as part of of the java grammar
			char lastChar=currentChar;
			currentChar = getNextChar();
			throw new InvalidCharacterException("error on character:"+lastChar); //an error token is returned
		}
	}
}
