package edu.ewencluley.javainterpreter.lexer.tokens;

import edu.ewencluley.javainterpreter.lexer.tokens.TokenTypes.BooleanOperationTypes;

public class BooleanOperationToken extends Token {
	
	TokenTypes.BooleanOperationTypes type;
	
	public TokenTypes.BooleanOperationTypes getType() {
		return type;
	}

	public BooleanOperationToken(String lexem, BooleanOperationTypes type) {
		super(lexem);
		this.type = type;
	}
}
