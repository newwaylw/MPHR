package uk.ac.shef.mphr;

import it.unimi.dsi.sux4j.mph.MinimalPerfectHashFunction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.BitSet;

import uk.ac.shef.util.MurmurHash;
import uk.ac.shef.util.Util;
import uk.ac.shef.util.LongBitSet;

public class MPHRLookup {
 private int bitsFingerprint; //number of bits used as fingerprint;
 private  int bitsRank;       //number of bits used as rank, e.g. 20bits  
 private LongBitSet bitSet;
 private MinimalPerfectHashFunction<String> mphf ;
 private CompactStore fpStore;
 private ArrayList<Integer> rankList;
 public MPHRLookup(int bitsFingerprint, int bitsRank){
	 fpStore = new CompactStore(bitsFingerprint, bitsRank);
	 this.bitsFingerprint = bitsFingerprint;
	 this.bitsRank = bitsRank;
 }
 
 /**
  * load the MPH function.
  * @param mphfObj
  */
 public void loadMPHF(String mphfObj){
	Object o = Util.readObject(mphfObj);
	 if(o instanceof MinimalPerfectHashFunction){
         this.mphf = (MinimalPerfectHashFunction<String>) o;
         
     }else{
         System.err.println(mphf+" is not a object of MinimalPerfectHashFunction! Please check.");
         System.exit(-1);
     }
 }
 
 public void loadDataBitSet(String bitSetFile){
    Object o = Util.readObject(bitSetFile);
    if (o instanceof LongBitSet){
    	this.bitSet = (LongBitSet) o;
    }else{
    	System.err.print(bitSetFile +" format error!");
    	this.bitSet = null;	
    }
 }
     
 /**
  * load the text file containing unique frequencies of the dataset (corpus)
  * from rank 0 to size -1;
  * @param rankTxtFile
  */
 public void loadRankFreqFile(String rankTxtFile){
	 	rankList = new ArrayList<Integer>();
//	 	this.rankList = new int[this.mphf.size()];
   		BufferedReader reader;
   		String aLine = new String();
   		int i=0;
   		try{
   		reader = new BufferedReader(new InputStreamReader(new FileInputStream(rankTxtFile), "UTF8"));
   		while( (aLine = reader.readLine())!=null){
   			rankList.add(Integer.parseInt(aLine));
   			i++;
   		}
   		}catch(IOException ioe){
   			ioe.printStackTrace();
   		}
   		System.out.println("Total:"+i+"unique ranks");
 }
 
 /**
  * finds the Rank of this key
  * @param key
  * @return - rank of this key, -1 if key doesn't exist
  */
      public int rankOf(String key){
    	  int fingerprint = fpStore.fp(key);
    	  //use only the low this.bitsFingerprint bits to match
 //   	  int fpMask = (int)Math.pow(2, this.bitsFingerprint)-1;
    	  //only the last bitsFingerprint bits are considered.
//    	  fingerprint &= fpMask;
    	  
//    	  fingerprint <<= (32 - this.bitsFingerprint);
    	  //fill back 0s to high end.
//    	  fingerprint >>>= (32 - this.bitsFingerprint);
    	  
    	  long offset = this.mphf.getLong(key) * ( this.bitsFingerprint+this.bitsRank);
    	  int storedFP = 0;
    	  int rank = 0;
    	  //get stored fingerprint and see if they match
    	  //If not match then this key is not presented in the stored key hash.
    	  for(int i=0;i<this.bitsFingerprint;i++){
    		  // is true (1) 
    		  if(this.bitSet.get(offset+i)){    			 
    			  //convert binary to dec...
    			  storedFP += Math.pow(2, this.bitsFingerprint - i -1);
    		  }  
    	  }
    	  
    	  if(storedFP == fingerprint){
    		  for(int i=0;i<this.bitsRank;i++){
        		  // is true (1) 
        		  if(this.bitSet.get(offset+this.bitsFingerprint+i)){
        			  
        			  rank += Math.pow(2, this.bitsRank-i -1);
        		  }
    	  }
    		  return rank;
    	}
    	  //this key not found//新华社 13775 -> rank 1
	    	//payload = 1022951425
    	    //offset = 48816096
    	  else return -1;
    	  
      }
      
      public int freqOf(String key){
    	  int r = this.rankOf(key);
    	  
    	  if(r==-1) return 0;
    	  return this.rankList.get(r);
      }
   public static void main(String args[]){
	   MPHRLookup mphrLookup = new MPHRLookup(16,16);
	   mphrLookup.loadMPHF(args[0]);
	   mphrLookup.loadDataBitSet(args[1]);
	   mphrLookup.loadRankFreqFile(args[2]);
	   
 //  String key = "●";
	   String key = "";
	   try{
	   while(!key.equalsIgnoreCase("quit")){	   
		   System.out.print("please input key to search for:");
		   BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
		   key = br.readLine();
 
		   System.out.println("rank of \""+key+"\"="+mphrLookup.rankOf(key));
		   System.out.println("freq of \""+key+"\"="+mphrLookup.freqOf(key));
	   }
	   }catch(IOException ioe){
		   ioe.printStackTrace();
	   }
   }
}
