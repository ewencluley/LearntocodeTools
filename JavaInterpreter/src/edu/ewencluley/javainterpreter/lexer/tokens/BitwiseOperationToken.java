package edu.ewencluley.javainterpreter.lexer.tokens;

import edu.ewencluley.javainterpreter.lexer.tokens.TokenTypes.BitwiseOperationTypes;

public class BitwiseOperationToken extends Token {
	
	BitwiseOperationTypes type;
	
	public BitwiseOperationToken(String lexem, BitwiseOperationTypes type) {
		super(lexem);
		this.type = type;
	}

}
