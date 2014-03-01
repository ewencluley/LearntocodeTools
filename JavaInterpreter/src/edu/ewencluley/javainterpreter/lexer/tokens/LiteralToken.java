package edu.ewencluley.javainterpreter.lexer.tokens;

public class LiteralToken extends Token {
	
	TokenTypes.LiteralTypes type;
	
	public LiteralToken(String lexem, TokenTypes.LiteralTypes type) {
		super(lexem);
		this.type = type;
	}

}
