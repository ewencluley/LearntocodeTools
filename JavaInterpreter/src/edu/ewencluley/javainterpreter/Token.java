package edu.ewencluley.javainterpreter;

/**
 * Token represents a lexical token consisting of a type and a lexem. 
 * It is used to represent tokens which are parsed by the Lexical Analyzer
 * @author 52694
 * @version 1.0 04/11/2010
 *
 */
public class Token 
{
	private String lexem;
	private String tokenType;

	/**
	 * Constructor for building a new token
	 * @param lexem String - the word identified to be a specific token
	 * @param type String - a general identifier type for the token, e.g. Error, Identifier, etc.
	 */
	public Token(String lexem, String type)
	{
		this.lexem = lexem;
		this.tokenType = type;
	}

	/**
	 * The equals method to make 2 Tokens equal if they have the same values in their fields, type and lexem.
	 * @param o - another object to compare it to.
	 * @return - true if the Tokens are equal and False otherwise.
	 */
	public boolean equals(Token o)
	{

		if(o.tokenType ==tokenType){
			return true;
		}else{
			return false;
		}

	}
	/**
	 * Gets a String representation of the Token using it's lexem and identifier
	 * @return - a String representation of the Token.
	 */
	public String toString()
	{
		return lexem + "<" + tokenType + ">";
	}

	public String getLexem()
	{
		return lexem;
	}
}
