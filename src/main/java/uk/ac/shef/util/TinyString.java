package uk.ac.shef.util;

import java.io.Serializable;

/**
 * String has too much overhead, this one simply wraps around a char array
 * to minimise memory overhead.
 * @author wei
 *
 */
public class TinyString implements CharSequence, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 0x80987ABCL;
	char[] charArray;
	
	/**
	 * convert String to TinyString to avoid added overhead
	 * @param str
	 */
	public TinyString(String str){
		this.charArray = str.toCharArray();
	}
	
	public TinyString(char[] charArray){
		this.charArray = charArray;
	}
	
	
	public String toString(){
		String str = new String(this.charArray);
		return str;
	}

	public char charAt(int i) {
		
		return this.charArray[i];
	}

	public int length() {
		return this.charArray.length;
	}

	public CharSequence subSequence(int start, int end) {
		int len = end - start;
		char[] array = new char[len];
		for(int i=0;i<len;i++)
			array[i] = this.charArray[start+i];
		return new TinyString(array);
	}
	
}
