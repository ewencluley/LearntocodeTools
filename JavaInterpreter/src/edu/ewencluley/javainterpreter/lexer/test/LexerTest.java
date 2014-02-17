package edu.ewencluley.javainterpreter.lexer.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.ewencluley.javainterpreter.lexer.Lexer;

public class LexerTest {

	@Test
	public void testTokenize() {
		testWith("System.out.println('Hello World');", 1);
		testWith("int x = 1;", 4);
		testWith("int x = 1;\n x = x + 1;", 9);
		testWith("public ClassName{ \nint x; \n}", 5);
	}
	
	private void testWith(String s, int expectedLength){
		List<String> tokens = Lexer.tokenize(s);
		System.out.println(tokens);
		assertEquals(tokens.size(), expectedLength);
	}

}
