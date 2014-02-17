package edu.ewencluley.javainterpreter.lexer;

public enum StatementType {
	FOR("for"), WHILE("while"), METHOD(""), CLASS("class"), STATEMENT, IMPORT("import");
	String keyword;
	private StatementType(String word){
		keyword = word;
	}
	
	private StatementType(){
		
	}
}
