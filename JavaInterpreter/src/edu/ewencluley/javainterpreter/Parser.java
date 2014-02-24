package edu.ewencluley.javainterpreter;

import java.io.*;
/**
 * The Parser Class has methods for taking in an input file and reading it character by character
 * @author 52694
 * @version 1.0 (27/10/10)
 *
 */
public class Parser 
{
	private Reader fileReader;

	/**
	 * Constructor for the Parser, takes an input file and reads it in.
	 * If the file is not found it will print out an error message to the terminal specifying this.
	 */
	public Parser(File f)
	{
		try{
			fileReader = new BufferedReader(new FileReader(f));
		} catch(FileNotFoundException ex){System.out.println("File Not Found");}
	}
	
	public Parser(String s)
	{
		fileReader = new StringReader(s);
	}

	/**
	 * Reads the next character in the text file and returns it.
	 * @return the next character found in the text file, as a string
	 * @throws IOException - if there is a problem with reading the file this exception will be thrown.
	 */
	public char readNextChar() throws IOException
	{
		int asciiCode = fileReader.read();
		if (asciiCode != -1){
			char current = (char) asciiCode; //converts ASCII into characters.
			return current;
		} else {
			return '\b'; //this is the backspace character which is being used to denote the end of file. 
			//The reason backspace is chosen is that it is unlikely to occur as a single character in a 
			//file which is being read in so should only be returned at the true end of file.
		}
	}
}
