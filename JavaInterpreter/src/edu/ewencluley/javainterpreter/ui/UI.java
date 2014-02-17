package edu.ewencluley.javainterpreter.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class UI {

	public static void main(String[] args){
		System.out.println("Enter something here : ");
		//JFrame frame = new JFrame();
		/*BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		String s = bufferRead.readLine();
		List<Line> lines = LineConstructor.splitLines(Lexer.tokenize(s));	 
		System.out.println(lines);*/
		HashMap<String, String> classes = edu.ewencluley.javainterpreter.AvailibleClassesInPath.getClasses();
		try {
			writeFile(classes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void writeFile(HashMap<String, String> classes) throws IOException{
		File file = new File("classes.txt");
		 
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(String s:classes.keySet()){
			if(s!=null){
				bw.write(s + " : " +classes.get(s));
				bw.newLine();
			}
		}
		bw.close();

		System.out.println("Done");
	}
	
}
