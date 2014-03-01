package edu.ewencluley.javainterpreter.lexer.tokens;

import edu.ewencluley.javainterpreter.lexer.tokens.TokenTypes.ConstructTypes;

public class ConstructToken extends Token {
	
	ConstructTypes type;
	
	

	public ConstructToken(String lexem, TokenTypes.ConstructTypes type) {
		super(lexem);
		this.type = type;
	}
	public ConstructTypes getType() {
		return type;
	}
}
