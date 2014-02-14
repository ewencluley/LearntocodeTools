package edu.ewencluley.javainterpreter.lexer.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.ewencluley.javainterpreter.lexer.Lexer;

public class LexerTest {

	@Test
	public void testTokenize() {
		assertEquals(Lexer.tokenize("System.out.println('Hello World');").size(), 1);
		assertEquals(Lexer.tokenize("int x = 1;").size(), 4);
		assertEquals(Lexer.tokenize("int x = 1;\n x = x + 1;").size(), 9);
		assertEquals(Lexer.tokenize("public ClassName{ \nint x; \n}").size(), 5);
	}

}
