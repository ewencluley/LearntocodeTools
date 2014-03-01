package edu.ewencluley.javainterpreter.lexer.tokens;
public class TokenTypes{
	public enum LiteralTypes {
		BOOLEAN, INTEGER, FLOAT, DOUBLE, LONG, SHORT, OBJECT, CHAR, STRING, NULL
	}
	
	public enum BooleanOperationTypes {
		AND, OR, NOT, INSTANCEOF, GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, EQUAL, NOT_EQUAL
	}
	
	public enum BitwiseOperationTypes {
		AND, OR, COMPLEMENT, XOR, LEFT_SHIFT_SIGNED, RIGHT_SHIFT_SIGNED, RIGHT_SHIFT_UNSIGNED
	}
	
	public enum ArithmeticOperationTypes {
		ADDITION, MULTIPLICATION, INC_DEC, MODULUS
	}
	
	public enum ConstructTypes {
		BLOCK_OPEN, BLOCK_CLOSE, PARENTHISIS, CLASS_DEF, ACCESS_MODIFIER, FOR, IF, ELSE, WHILE, SWITCH, CASE, LINE_END, SEMI_COLON, COMMA, RETURN, IMPLEMENTS, EXTENDS, DOT, STATIC, ABSTRACT, SQ_PARENTHISIS, NEW
	}
}
