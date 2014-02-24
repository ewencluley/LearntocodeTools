package edu.ewencluley.javainterpreter;

import java.io.*;
import java.util.HashMap;
import java.util.Stack;

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


	//***********************************************************************************************************
	//***********************************************************************************************************
	//*****************************START OF RECOGNIZER METHODS***************************************************
	//***********************************************************************************************************
	//***********************************************************************************************************
	private void factor()
	{
		if(token.equals(new Token("", "Constant"))){
			recognised("Factor:Constant");
			if(nodeStack.peek() != null){
				//check if there is a 's-' on the stack before the constant to be added. this will make it a negative number
				if(nodeStack.peek().getRoot().equals("s-") && nodeStack.peek().isLeaf()){
					nodeStack.pop();
					nodeStack.push(new SyntaxTreeNode("-"+token.getLexem()));
				} else {
					nodeStack.push(new SyntaxTreeNode(token.getLexem())); // if its not got a - before it, just put it in
				}
			}else{
				nodeStack.push(new SyntaxTreeNode(token.getLexem()));// if its going to be the first statement in the stack, just put it in
			}

		} else if(token.equals(new Token("", "Identifier"))){
			//checks if the identifier has been declared, i.e. is in the Symbol Table
			if(!symbolTable.containsKey(token.getLexem())){
				error("Variable not declared:"+token.getLexem());
			}else{
				//nodeStack.push(new SyntaxTreeNode(token.getLexem()));
				recognised("Factor:Identifier");
				if(!nodeStack.isEmpty()){
					//check if there is a 's-' on the stack before the constant to be added. this will make it a negative number
					if(nodeStack.peek().getRoot().equals("s-") && nodeStack.peek().isLeaf()){
						nodeStack.pop();
						nodeStack.push(new SyntaxTreeNode("-"+token.getLexem()));
					} else {
						nodeStack.push(new SyntaxTreeNode(token.getLexem())); // if its not got a - before it, just put it in
					}
				}else{
					nodeStack.push(new SyntaxTreeNode(token.getLexem()));// if its going to be the first statement in the stack, just put it in
				}
			}

		} else if(token.equals(new Token("(", "OpenParenthesis"))){
			getNextToken();
			expression();

			if(token.equals(new Token(")", "CloseParenthesis"))){
				recognised("Factor:(expression)");
			} else {
				error("')' expected");
			}
		}else{
			error("Factor expected.");
		}
		recognised("Factor");
	}
	private void divTerm()
	{
		factor();
		getNextToken();
		while(token.equals(new Token("*/", "MultOp"))&&token.getLexem().equals("/")){
			nodeStack.push(new SyntaxTreeNode(token.getLexem()));
			getNextToken();
			factor();
			getNextToken();

			statementBuild();
		}
		recognised("Term");
	}

	private void term()
	{
		divTerm();
		while(token.equals(new Token("*/", "MultOp"))&&token.getLexem().equals("*")){
			nodeStack.push(new SyntaxTreeNode(token.getLexem()));
			getNextToken();
			divTerm();

			statementBuild();
		}
		recognised("Term");
	}

	private void addExpression()
	{
		if(token.equals(new Token("+-", "AddOp"))){
			SyntaxTreeNode sign = new SyntaxTreeNode("s"+ token.getLexem()); //s- or s+ is used to denote a sign for a constant or an identifier.
			nodeStack.push(sign);
			getNextToken();
		}
		term();
		while(token.equals(new Token("+-", "AddOp"))&&token.getLexem().equals("+")){
			nodeStack.push(new SyntaxTreeNode(token.getLexem()));
			getNextToken();
			term();

			statementBuild();
		}
		recognised("Expression");
	}

	private void expression()
	{
		if(token.equals(new Token("+-", "AddOp"))){
			SyntaxTreeNode sign = new SyntaxTreeNode("s"+ token.getLexem()); //s- or s+ is used to denote a sign for a constant or an identifier.
			nodeStack.push(sign);
			getNextToken();
		}
		addExpression();
		while(token.equals(new Token("+-", "AddOp"))&&token.getLexem().equals("-")){
			nodeStack.push(new SyntaxTreeNode(token.getLexem()));
			getNextToken();
			addExpression();

			statementBuild();
		}
		recognised("Expression");
	}

	private void assignment()
	{
		if(token.equals(new Token("", "Identifier"))){
			factor();
		}else{
			error("Identifier Expected");
		}
		getNextToken();
		if(token.equals(new Token("=", "AssignmentOperator"))){
			nodeStack.push(new SyntaxTreeNode(token.getLexem()));//adds the equals operator
			recognised("Assignment");
		}else{
			error("Assignment Operator Expected");
		}
		getNextToken();
		expression();

		statementBuild();
	}

	private void statement()
	{
		if(token.equals(new Token("", "Identifier"))&& nextToken.equals(new Token("=", "AssignmentOperator"))){
			assignment();
			recognised("Statement");
			nodeStack.push(new SyntaxTreeNode("Statement"));//adds the statement node
			nodeStack.push(null);//adds the null for the trees right node.
			statementBuild();
		} else if(token.equals(new Token("print", "PrintReservedWord"))){
			printStatement();
			recognised("Statement");
			nodeStack.push(new SyntaxTreeNode("Statement"));//adds the statement node
			nodeStack.push(null);//adds the null for the trees right node.
			statementBuild();
		} else {
			empty();
		}


	}

	private void statementlist()
	{
		statement();
		while(token.equals(new Token(";", "Semi-Colon"))){
			getNextToken();
			statement();
		}
		buildTree();
		recognised("Statement List");
	}

	private void printStatement()
	{
		if(token.equals(new Token("print", "PrintReservedWord"))){
			nodeStack.push(null);
			nodeStack.push(new SyntaxTreeNode(token.getLexem()));
			getNextToken();
			recognised("Print statement");

		} else {
			error("Print command expected");
		}
		expression();
		statementBuild();
	}

	private void iddef()
	{
		if(token.equals(new Token("", "Identifier"))){
			//adds an identifier to the symbol table if not in it already. the locations in memory are stored 
			//as a coefficient of their address within the section of memory specified for storage.
			if(!symbolTable.containsKey(token.getLexem())){
				symbolTable.put(token.getLexem(), symbolTable.size());
			}else{
				error("Variable already declared");
			}
			if(nextToken.equals(new Token("=", "AssignmentOperator"))){//avoids adding the identifier if it is simply part of a declaration
				nodeStack.push(new SyntaxTreeNode(token.getLexem()));
			}
			getNextToken();
			recognised("iddef");
		} else {
			error("Identifier Expected");
		}
		if(token.equals(new Token("=", "AssignmentOperator"))){
			//if a iddef has an = in it then it is an assignment and must be added to the syntax tree. 
			//simple identifiers without assignment do not.
			nodeStack.push(new SyntaxTreeNode(token.getLexem()));
			getNextToken();
			expression();
			statementBuild();
			//it also needs to be built into a statement node so it can be added to the statement list in a logical fashion.
			nodeStack.push(new SyntaxTreeNode("Statement"));
			nodeStack.push(null);
			statementBuild();
		}

	}

	private void empty()
	{
		recognised("empty");
	}

	private void varDecl()
	{
		if(token.equals(new Token("int", "DeclarationReservedWord"))){
			getNextToken();
			iddef();
			while(token.equals(new Token(",", "Comma"))){
				getNextToken();
				iddef();
			}
			recognised("VarDecl");
		}else{
			error("Variable not declared");
		}
	}

	private void block()
	{
		if(token.equals(new Token("{", "BlockOpen"))){
			getNextToken();
			if(token.equals(new Token("int", "DeclarationReservedWord"))){
				varDecl();
				getNextToken();
			}

			statementlist();
			if(token.equals(new Token("}", "BlockClose"))){
				recognised("Block");
			}else{
				error("} expected");
			}
		}else{
			error("{ expected. No program block found");
		}

	}

	public boolean program()
	{
		block();
		if(errorCount > 0){
			return false;
		}else{
			System.out.println("**********************");
			System.out.println("*****SYNTAX TREE******");
			System.out.println("**********************");
			preorder(treeRoot, 0);//prints out the syntax tree if the program is syntactically correct
			return true;
		}
	}

	//***********************************************************************************************************
	//***********************************************************************************************************
	//*****************************END OF RECOGNIZER METHODS*****************************************************
	//***********************************************************************************************************
	//***********************************************************************************************************

	private void getNextToken()
	{
		token = nextToken; //sets current token to the old next token
		nextToken = theLex.getToken(); // sets the next token to the next token
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
		System.out.println(r+" recognised.");
	}

	//builds a statement from the top 3 nodes on the stack.
	private void statementBuild()
	{
		if(!nodeStack.isEmpty()){
			SyntaxTreeNode right = nodeStack.pop();
			if(!nodeStack.isEmpty()){
				nodeStack.peek().setRight(right);
				SyntaxTreeNode root = nodeStack.pop();
				if(!nodeStack.isEmpty()){
					root.setLeft(nodeStack.pop());
					nodeStack.push(root);
				}
			}
		}
	}

	//used to build a stack of statement nodes into a tree with the next statement as the right hand child of the current 
	private void buildTree()
	{
		while(nodeStack.size()> 1){
			SyntaxTreeNode right = nodeStack.pop();
			nodeStack.peek().setRight(right);
		}
		if(!nodeStack.isEmpty()){
			treeRoot = nodeStack.pop();
		}
	}
	/**
	 * Used to get the root node with the syntax tree attached
	 * @return the syntax tree, null if not tree generated
	 */
	public SyntaxTreeNode getRootNode()
	{
		return treeRoot;
	}

	/**
	 * Gets the symbolTable for use by other objects, e.g. code generation
	 * @return symbolTable, a HashMap.
	 */
	public HashMap<String, Integer> getSymbolTable()
	{
		return symbolTable;
	}

	/**
	 * Prints out the syntax tree using a recursive depth first preorder algorithm.
	 * @param n the node to analyze
	 * @param i the number of calls to the algorithm, e.g. for the root node this will be 0, for it's children, 1, etc.
	 */
	public void preorder(SyntaxTreeNode n, int i)
	{
		//correctly aligns the tabs to make the tree display correctly
		String tabs = "";
		for(int x=0; x<i; x++){
			tabs+="  ";
		}
		System.out.println(tabs + n);
		if(n.getDaughter("left") != null && n.getDaughter("left").getRoot() != "dummy"){ //dummy nodes are returned by the SyntaxTreeNodes  
			System.out.print("LHS:");													 //instead of null objects. This means properties can 
			preorder(n.getDaughter("left"), i+1);										 //be checked without NullPointerExceptions occuring.
		}																				 //These dummys are ignored and not printed.
		if(n.getDaughter("right") != null && n.getDaughter("right").getRoot() != "dummy"){
			System.out.print("RHS:");
			preorder(n.getDaughter("right"), i+1);
		}
	}


}
