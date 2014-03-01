package edu.ewencluley.javainterpreter;

import java.io.*;
import java.util.HashMap;
import java.util.Stack;

import edu.ewencluley.javainterpreter.exceptions.InvalidCharacterException;
import edu.ewencluley.javainterpreter.lexer.tokens.ArithmeticOperationToken;
import edu.ewencluley.javainterpreter.lexer.tokens.AssignmentToken;
import edu.ewencluley.javainterpreter.lexer.tokens.BitwiseOperationToken;
import edu.ewencluley.javainterpreter.lexer.tokens.BooleanOperationToken;
import edu.ewencluley.javainterpreter.lexer.tokens.ConstructToken;
import edu.ewencluley.javainterpreter.lexer.tokens.IdentifierToken;
import edu.ewencluley.javainterpreter.lexer.tokens.LiteralToken;
import edu.ewencluley.javainterpreter.lexer.tokens.Token;
import edu.ewencluley.javainterpreter.lexer.tokens.TokenTypes;
import edu.ewencluley.javainterpreter.lexer.tokens.TypeToken;

/**SyntaxAnalyser will get tokens from the Lexical Analyser and see if they adhear to the dl0 syntax. 
 * It works as a top down recursive parser. It generates and prints a parse tree this can be used by a code generator to create code.
 * 
 */
public class SyntaxAnalyser {

	HashMap<String, Integer> symbolTable; // stores the location offset for each identifier this 
										  //will be used to find the correct location of each identifier in machine memory
	Stack<SyntaxTreeNode> nodeStack; 	  //stores the stack of tree nodes that it has found
	Token token;
	Token nextToken;
	LexicalAnalyzer theLex;
	SyntaxTreeNode treeRoot;
	int errorCount =0;

	/**
	 * Creates a new syntax analyser object with an input file
	 * @param File f - the file to be compiled
	 */
	public SyntaxAnalyser(File f)
	{
		theLex = new LexicalAnalyzer(f);
		symbolTable=new HashMap<String, Integer>();
		nodeStack = new Stack<SyntaxTreeNode>();
		getNextToken();
		getNextToken();
	}

	public SyntaxAnalyser(String text) {
		theLex = new LexicalAnalyzer(text);
		symbolTable=new HashMap<String, Integer>();
		nodeStack = new Stack<SyntaxTreeNode>();
		getNextToken();
		getNextToken();
	}
	
	public void start(){
		statement();
	}


	//***********************************************************************************************************
	//***********************************************************************************************************
	//*****************************START OF RECOGNIZER METHODS***************************************************
	//***********************************************************************************************************
	//***********************************************************************************************************
	
	private void varDeclaration(){
		if(token instanceof TypeToken){
			getNextToken();
			if(token instanceof IdentifierToken){
				getNextToken();
				if(token instanceof ConstructToken && ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.SEMI_COLON)){
					recognised("var declaration");
					getNextToken();
				}else if(token instanceof AssignmentToken){
					getNextToken();
					expression();
					if(token instanceof ConstructToken && ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.SEMI_COLON)){
						recognised("var declaration with assignment");
						getNextToken();
					}else{
						error("expected ; after expression");
					}
				}
			}
		}
	}
	
	private void type(){
		if(token instanceof TypeToken){
			getNextToken();
			recognised("Type");
		}
	}
	
	
	private void expression(){
		//IDENTIFIER
		if(token instanceof IdentifierToken && !operator(nextToken)){
			getNextToken();
			recognised("Expression - Identifier");
		}//LITERAL
		else if(token instanceof LiteralToken && !operator(nextToken)){
			getNextToken();
			recognised("Expression - Literal");
		}//( EXPRESSION )
		else if(token instanceof ConstructToken && 
				((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.PARENTHISIS) && 
				token.getLexem().equals("(")){
			getNextToken();
			expression();
			if(token instanceof ConstructToken && 
					((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.PARENTHISIS) && 
					token.getLexem().equals(")")){
				getNextToken();
				recognised("Expression - ( Expression )");
			}
		}//NOT EXPRESSION
		else if(token instanceof BooleanOperationToken &&
				((BooleanOperationToken)token).getType().equals(TokenTypes.BooleanOperationTypes.NOT)){
			getNextToken();
			expression();
			recognised("Expression - ! Expression");
		}//NEW TYPE OR ARRAY
		else if(token instanceof ConstructToken && 
				((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.NEW)){
			getNextToken();
			if(token instanceof TypeToken){
				getNextToken();
				if(token instanceof ConstructToken && ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.PARENTHISIS)){
					if(((ConstructToken)token).getLexem().equals("(")){
						getNextToken();
						if(!((ConstructToken)token).getLexem().equals(")")){
							getNextToken();
							expression();
							if(((ConstructToken)token).getLexem().equals(")")){
								getNextToken();
								recognised("Expression - new type ( expression )");
							}
						}else{
							getNextToken();
							recognised("Expression - new type ()");
						}
					}else if(((ConstructToken)token).getLexem().equals("[")){
						getNextToken();
						if(!((ConstructToken)token).getLexem().equals("]")){
							getNextToken();
							expression();
							if(((ConstructToken)token).getLexem().equals("]")){
								getNextToken();
								recognised("Expression - new type [ expression ]");
							}
						}else{
							getNextToken();
							error("expected expression in new type []");
						}
					}
				}
			}
		}
		//EXPRESSION OPERATOR EXPRESSION
		else if(token instanceof LiteralToken || token instanceof IdentifierToken){
			getNextToken();
			if(operator(token)){
				getNextToken();
				expression();
				recognised("Expression - expression operator expression");
			}
		}else{
			error("Expression - expected expression");
		}
	}
	
	private void statement(){
		//{ STATEMENT }
		if(token instanceof ConstructToken
				&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.BLOCK_OPEN)){
			getNextToken();
			statement();
			while(!(token instanceof ConstructToken)
					|| !((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.BLOCK_CLOSE)){
				statement();
			}
			if(token instanceof ConstructToken
					&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.BLOCK_CLOSE)){
				getNextToken();
				recognised("Statement- { Statement }");
			}
		}
		//IF STATEMENT
		else if(token instanceof ConstructToken
				&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.IF)){
			if(token instanceof ConstructToken
					&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.PARENTHISIS)
					&& ((ConstructToken)token).getLexem().equals("(")){
				getNextToken();
				expression();
				if(token instanceof ConstructToken
						&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.PARENTHISIS)
						&& ((ConstructToken)token).getLexem().equals(")")){
					getNextToken();
					statement();
					if(token instanceof ConstructToken
							&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.ELSE)){
						getNextToken();
						statement();
						recognised("Statement - if else construct");
					}else{
						recognised("Statement - if construct");
					}
				}
			}
		}
		//WHILE STATEMENT
		else if(token instanceof ConstructToken
				&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.WHILE)){
			if(token instanceof ConstructToken
					&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.PARENTHISIS)
					&& ((ConstructToken)token).getLexem().equals("(")){
				getNextToken();
				expression();
				if(token instanceof ConstructToken
						&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.PARENTHISIS)
						&& ((ConstructToken)token).getLexem().equals(")")){
					getNextToken();
					statement();
					recognised("Statement - while construct");
				}
			}
		}
		
		//TODO FOR loop
		//TODO FOR EACH loop
		
		//ASSIGNMENT STATEMENT
		else if(token instanceof IdentifierToken && nextToken instanceof AssignmentToken){
			getNextToken();//move past identifier
			getNextToken();//move past equals
			expression();
			if(token instanceof ConstructToken
					&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.SEMI_COLON)){
				recognised("Statement - assignment statement");
				getNextToken();
			}
		}
		//ARRAY ASSIGNMENT STATEMENT
		else if(token instanceof IdentifierToken 
				&& nextToken instanceof ConstructToken 
				&& ((ConstructToken)nextToken).getType().equals(TokenTypes.ConstructTypes.SQ_PARENTHISIS)
				&& ((ConstructToken)nextToken).getLexem().equals("[")){
			getNextToken();//move past identifier
			getNextToken();//move past [
			expression();
			if(token instanceof IdentifierToken 
					&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.SQ_PARENTHISIS)
					&& ((ConstructToken)token).getLexem().equals("]")){
				getNextToken();
				if(token instanceof AssignmentToken){
					getNextToken();//move past =
					expression();
					if(token instanceof ConstructToken
							&& ((ConstructToken)token).getType().equals(TokenTypes.ConstructTypes.SEMI_COLON)){
						recognised("Statement - array assignment statement");
					}else{
						error("Statement - expected expression after =");
					}
				}else{
					error("Statement - expected assignment");
				}
				recognised("Statement - array assignment statement");
			}else{
				error("Statement - expected ] after expression");
			}
			recognised("Statement - statement");
		}
		//VAR DECAL STATEMENT
		else if(token instanceof TypeToken ){
			varDeclaration();
		}
		else{
			error("Statement -expected statement");
		}
	}
	
	private boolean operator(Token token){
		if(token instanceof ArithmeticOperationToken 
				|| token instanceof BooleanOperationToken
				|| token instanceof BitwiseOperationToken){
			return true;
		}
		return false;
	}
	
	
	

	//***********************************************************************************************************
	//***********************************************************************************************************
	//*****************************END OF RECOGNIZER METHODS*****************************************************
	//***********************************************************************************************************
	//***********************************************************************************************************

	private void getNextToken()
	{
		token = nextToken; //sets current token to the old next token
		try {
			nextToken = theLex.getToken();
		} catch (InvalidCharacterException e) {
			e.printStackTrace();
		} // sets the next token to the next token
		if(token!=null){
			System.out.println("Token: "+token); //prints out the token
		}
	}

	/**
	 * Used to keep track of errors and generate error messages
	 * @param e the error String to print out
	 */
	private void error(String e)
	{
		errorCount +=1;
		System.err.println("Error:"+e);
	}

	private void recognised(String r)
	{
		System.out.println("Recognised:"+r);
	}

	


}
