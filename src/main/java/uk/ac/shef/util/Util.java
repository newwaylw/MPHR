package uk.ac.shef.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Util {

	/**
	 * each line of text corresponds to an element in the List.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static  ArrayList<String>  readFile  (String file) throws IOException{
		ArrayList<String> sentenceList = new ArrayList<String>();
		
//		PrintStream out = new PrintStream(System.out, true, "UTF-8");
		BufferedReader reader;
		StringBuffer strBuffer = new StringBuffer();
		String cnLine;
		
		reader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(file), "UTF8"));
		int no = 0;
		while( (cnLine = reader.readLine()) != null){
			if(cnLine.length()==0) continue;
			no++;
			strBuffer.append(cnLine);
			//Unicode header
			if(strBuffer.charAt(0) == 0xFEFF){
				strBuffer.deleteCharAt(0);
			}
			cnLine = strBuffer.toString();
			
			sentenceList.add(cnLine);
			strBuffer.setLength(0);
//			out.println("line number:"+no+ "  "+cnLine);
		}
//		System.out.println("line number:"+no);
		reader.close();
		return sentenceList;
	}

	/**
	 * serialize object <code>o</code> to destination <code>dst</code>
	 * @param o
	 * @param dst  file path (name)
	 */
	public static void serializeObject(Object o, String dst){
		// Write to disk with FileOutputStream
		try{
		FileOutputStream f_out = new FileOutputStream(dst);

		// Write object with ObjectOutputStream
		ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

		// Write object out to disk
		obj_out.writeObject ( o);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public static Object readObject(String src){
		try{
			FileInputStream f_in = new FileInputStream(src);

			// Write object with ObjectOutputStream
			ObjectInputStream obj_in = new ObjectInputStream (f_in);

			// Write object out to disk
			Object o = obj_in.readObject();
			return o;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
	}
	
	public static int getUnicodeDec(char c){
		return (int)c;
	}
	
	
	
	
	public static void main(String[] args){
		int i = 14;
		System.out.println(i<<2);
		
	}
}
