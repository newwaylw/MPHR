package uk.ac.shef.util;

import java.util.ArrayList;
import java.util.BitSet;

public class Temp {

	public static void main(String args[]){
		/*
		int len = 8;
		int v = 0;
		BitSet bitSet = new BitSet(len);
		bitSet.set(7);
		bitSet.set(0);
		for(int i=0;i<len;i++){
  		  // is true (1) 
  		  
  		  if(bitSet.get(i)){    			 
  			  //convert binary to dec...
  			  v += Math.pow(2, i);
  			  //v += tmp;
  		  }  
  	  }
		System.out.println(bitSet.toString()+"->"+Integer.toBinaryString(v)+"="+v );
		
		int tmp = 1928190660;
		System.out.println("To test:"+Integer.toBinaryString(tmp)+" = "+tmp);
		int i = 2;
		tmp <<= i;
		
		System.out.println("       "+Integer.toBinaryString(tmp)+" = "+tmp);
		tmp >>>=i;
		System.out.println("         "+Integer.toBinaryString(tmp)+" = "+tmp);
		int mask = 2;
		int test = -1;
		System.out.println(test & mask);
		*/
		int n = Integer.parseInt(args[0]);
		int k=100000;
		char[][] array = new char[n][3];
		System.gc();
//		ArrayList<TinyString> list = new ArrayList<TinyString>(n);
//		char[] array = new char[3];

		for(int i=0;i<n;i++){
		    	
		    	array[i][0] = 123;
		    	array[i][1] = 'b';
		    	array[i][2] = '我';
//		    	list.add(new TinyString(array));
//		    	System.gc();
		    	
		    	if(i%k == 0){
		    		 long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			         m1 /= (1024*1024);
			         System.out.println("memory used  when "+i+" key inserted ="+m1+" MBytes.");
		    	}
		    }
		    
			
//			System.exit(1);
		
	}
}
