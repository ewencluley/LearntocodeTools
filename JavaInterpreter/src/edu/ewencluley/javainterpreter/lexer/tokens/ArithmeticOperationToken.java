package edu.ewencluley.javainterpreter.lexer.tokens;

import edu.ewencluley.javainterpreter.lexer.tokens.TokenTypes.ArithmeticOperationTypes;

public class ArithmeticOperationToken extends Token {
	
	ArithmeticOperationTypes type;
	
	public ArithmeticOperationToken(String lexem, ArithmeticOperationTypes type) {
		super(lexem);
		this.type = type;
	}

}
