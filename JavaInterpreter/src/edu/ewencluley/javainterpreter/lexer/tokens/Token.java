package edu.ewencluley.javainterpreter.lexer.tokens;

/**
 * Token represents a lexical token consisting of a type and a lexem. 
 * It is used to represent tokens which are parsed by the Lexical Analyzer
 * @author 52694
 * @version 1.0 04/11/2010
 *
 */
public abstract class Token 
{
	private String lexem;

	/**
	 * Constructor for building a new token
	 * @param lexem String - the word identified to be a specific token
	 * @param type String - a general identifier type for the token, e.g. Error, Identifier, etc.
	 */
	public Token(String lexem)
	{
		this.lexem =lexem;
	}

	public String getLexem() {
		return lexem;
	}
	
	public String toString(){
		return (this.getClass().toString() + " - ['"+lexem+"']");
	}
}
