package edu.ewencluley.javainterpreter.exceptions;

public class InvalidTypeException extends Exception {
	private String typeName;
	private int lineNo;

	public InvalidTypeException(String typeName, int lineNo){
		this.typeName = typeName;
		this.lineNo = lineNo;
	}
}
