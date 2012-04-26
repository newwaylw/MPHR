package uk.ac.shef.languagemodel;

import it.unimi.dsi.sux4j.mph.MinimalPerfectHashFunction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.BitSet;

import uk.ac.shef.mphr.CompactStore;
import uk.ac.shef.util.LongBitSet;
//import uk.ac.shef.languagemodel.DatabaseLanguageModel;
//import uk.ac.shef.util.MurmurHash;
import uk.ac.shef.util.Util;

/**
 * Ngram language model using Minimum Perfect Hash Function
 * @author wei
 *
 */
public class MPHRLanguageModel extends AbstractLanguageModel{
 private int bitsFingerprint; //number of bits used as fingerprint;
 private  int bitsRank;            //number of bits used as rank, 20bits  
 private LongBitSet bitSet;
 private MinimalPerfectHashFunction<String> mphf ;
 private CompactStore fpStore;
 private ArrayList<Long> rankList;
// private DatabaseLanguageModel dbLM;
// public static final double ALPHA = 0.995;
 
 //total token number
 public static final int TOKEN = 1359344942;
 public static final int BIGRAM_TYPE = 4045650;
 public static final int VOCA = 9196;
// private int N1 = 126928215;
// private int N2 = 28500868;
// private double d;
 
 /**
  * Constructor
  * @param bitsFingerprint
  * @param bitsRank
  */
 public MPHRLanguageModel(int bitsFingerprint, int bitsRank){
	 fpStore = new CompactStore (bitsFingerprint, bitsRank);
	 this.bitsFingerprint = bitsFingerprint;
	 this.bitsRank = bitsRank;
	 this.rankList = new ArrayList<Long>();
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
 
 /**
  * Load the bitset
  * @param bitSetFile
  */
 public void loadDataBitSet(String bitSetFile){
    Object o = Util.readObject(bitSetFile);   
    
    if (o instanceof LongBitSet){
    	this.bitSet = (LongBitSet) o;
//    	this.totalToken = this.totalToken();
    } else{
    	this.bitSet = null;
    	System.err.println("Error: "+bitSetFile+" is not a valid LongBitSet serialization!");
    	System.exit(-1);
    }
 }

 /**
  * load the text file containing unique frequencies of the dataset (corpus)
  * from rank 0 to size -1;
  * @param rankTxtFile
  */
 public int loadRankFreqFile(String rankTxtFile){
//	 	this.rankList = new int[this.mphf.size()];
   		BufferedReader reader;
   		String aLine = new String();
   		int i=0;
   		try{
   		reader = new BufferedReader(new InputStreamReader(new FileInputStream(rankTxtFile), "UTF8"));
   		while( (aLine = reader.readLine())!=null){
   			rankList.add(Long.parseLong(aLine));
   			i++;
   		}
   		}catch(IOException ioe){
   			ioe.printStackTrace();
   		}
   		return i;
 }
 
 /**
  * load the serialised object containing unique frequencies of the dataset (corpus)
  * @param rankObjFile
  * @return number of unique frequencies.
  */
 @SuppressWarnings("unchecked")
public int loadRankFreqObject(String rankObjFile){
	 Object o = Util.readObject(rankObjFile);
	 if(o instanceof ArrayList<?>){
		 this.rankList = (ArrayList<Long>) o;
	 }
	 return this.rankList.size();
 }
 
 
 /**
  * return how many elements are stored in this model 
  * by looking at the length of the bitset.
  * @return
  */
 public long numElement(){
	long length = this.bitSet.capacity();
	return length/(this.bitsFingerprint+this.bitsRank);
 }
 /**
  * finds the Rank of this key
  * @param key
  * @return - rank of this key, -1 if key doesn't exist
  */
      public int rankOf(String key){
    	  long fingerprint = fpStore.fp(key);
    	  long index = this.mphf.getLong(key) * ( this.bitsFingerprint+this.bitsRank);
    	  long storedFP = 0;
    	  int rank = 0;
    	  //get stored fingerprint and see if they match
    	  //If not match then this key is not presented in the stored key hash.
    	  for(int i=0;i<this.bitsFingerprint;i++){
    		  // is true (1) 
    		  if(this.bitSet.get(index+i)){    			 
    			  //convert binary to dec...
    			  storedFP += Math.pow(2, this.bitsFingerprint - i -1);
    		  }  
    	  }
    	  
    	  if(storedFP == fingerprint){
    		  for(int i=0;i<this.bitsRank;i++){
        		  // is true (1) 
        		  if(this.bitSet.get(index+this.bitsFingerprint+i)){
        			  
        			  rank += Math.pow(2, this.bitsRank-i -1);
        		  }
    	  }
    		  return rank;
    	}
    	  //this key not found
    	  else return -1;
    	  
      }
      
      /**
       * return the number of total tokens in the model,
       * it iterates every rank and sum up the frequencies.
       * @return
       */
      public long getTotalToken(){
    	  return this.TOKEN;
    	  /*
    	  long index = this.bitsFingerprint;
    	  long numElement =  this.numElement();
    	  long total = 0;
    	  int rank;
    	  int count = 0;
    	  long end = numElement * (this.bitsFingerprint+this.bitsRank);
    	 while (index < end){
    		 count ++;
    		 rank = 0;
    		 for(int i=0;i<this.bitsRank;i++){
       		  // is true (1) 
       		  if(this.bitSet.get(index+i)){      			  
       			  rank += Math.pow(2, this.bitsRank-i -1);
       		  }
    		 }
    		 total+=this.rankList.get(rank);
    		 index+=(this.bitsRank+this.bitsFingerprint);
    	 }
    	 System.out.println(count + " elements iterated.");
    	 return total;
    	 */
      }
      
      /**
       * return the count (frequency) associated with the given key
       */
      public long getCount(String key){
    	  int r = this.rankOf(key);
    	  
    	  if(r==-1) return 0;
    	  
    	  return this.rankList.get(r);
      }

      /**
       * Interpolated Kneser-Ney smoothing up to 4gram
       * @param word
       * @return
       */
        public double getIKNProb(String word){
    	  int wLen = word.length();
    	  int n1 = 126928215;
    	  int n2 = 28500868;
    	  double d =  (n1*1.0) / (2*n1+n2);
//    	  double d = 0.98;
    	  long v = 0;
    	  long v1 = 0;
    	  long v2 = 0;
    	  double gamma=0;
    	  long count=0;
    	  long subCount = 0;
    	  char w1,w2,w3,w4;
    	  double p=0.0;
    	  
    	  switch(wLen){
    	  
    	  case 1: w1 = word.charAt(0); 
    	  p = (Math.max(0, this.getCount(w1+"")-d) + d)/this.TOKEN; break;
    	  
    	  case 2:
    		  w1 = word.charAt(0); 
    		  w2 = word.charAt(1);
    		  //IKN unigram V(* wi)
        	  v1 = this.getCount("*"+ w2);
        	 p = (Math.max(0, v1-d) + d)/MPHRLanguageModel.BIGRAM_TYPE;
        	   
        	  //C(wi-1 *）
        	  subCount = this.getCount(w1+"");
        	  count = this.getCount(""+w1+w2);
  
        	  if(subCount>0 && subCount >=count){
        		  //IKN bigram V(wi-1,*)
        		  
            	  v1 = this.getCount(w1+"*");      
            	  gamma = v1*d/subCount;
        		  p*=gamma;				//   V(wi-1,*)*D/V(*,wi-1,*) * pikn(wi)				
        		  p+=Math.max(0, count-d)/subCount;
        	  }
        	  break;
        	  
        	  case 3:
        		  w1 = word.charAt(0); 
        		  w2 = word.charAt(1);
        		  w3 = word.charAt(2);
        	  
            	  //IKN unigram V(* wi)
            	  v1 = this.getCount("*"+ w3);
            	  p = (Math.max(0, v1-d) + d)/MPHRLanguageModel.BIGRAM_TYPE;
            	             	 
            	//V(*,wi-1);
            	  v2 = this.getCount("*"+w2+"*");
            	  
            	  //IKN bigram V(wi-1,*)
            	  v1 = this.getCount(w2+"*");
            	  if(v2>0){
            	  p*=v1*d/v2;				//   V(wi-1,*)*D/V(*,wi-1,*) * pikn(wi)				
            	  v= this.getCount("*"+w2+w3); // V(*,wi-1,wi)
         //   	  v2= this.dbLM.queryResultRows("*", word.substring(wLen-2,1));
            	  p+=Math.max(0, v-d)/v2;
            	  }
            	  
            	 
            	  //C(wi-2,wi-1, *）
            	 subCount = this.getCount(""+w1+w2);
            	 count = this.getCount(""+w1+w2+w3);

            	 if(subCount>0 && subCount >=count){
            		 //IKN trigram   V(wi-2,wi-1,*);
               	  	  v1= this.getCount(""+w1+w2+"*");
            		  p*=v1*d/subCount;				//   V(wi-1,*)*D/V(*,wi-1,*) * pikn(wi)				
            		  p+=Math.max(0, count-d)/subCount;
            	  }
            	  break;
            	  
        	  case 4:
        		  w1 = word.charAt(0); 
        		  w2 = word.charAt(1);
        		  w3 = word.charAt(2);
        		  w4 = word.charAt(3);
            	  
        		  //check if 4gram context exists p(w4|w1w2w3) -- C(w1w2w3*) == 0 ? 

        	   	  //IKN unigram V(* wi)
            	  v1 = this.getCount("*"+ w4);
            	  p = (Math.max(0, v1-d) + d)/this.BIGRAM_TYPE;
            	  
            	  //IKN bigram V(wi-1,*)
            	  v1 = this.getCount(w3+"*");
            	  //V(*,wi-1);
            	  v2 = this.getCount("*"+w3+"*");
            	  
            	  if(v2>0){
            	  p*=v1*d/v2;				//   V(wi-1,*)*D/V(*,wi-1,*) * pikn(wi)				
            	  v1= this.getCount("*"+w3+w4); // V(*,wi-1,wi)
         //   	  v2= this.dbLM.queryResultRows("*", word.substring(wLen-2,1));
            	  p+=Math.max(0, v1-d)/v2;
            	  }
            	  
            	  //IKN trigram   V(wi-2,wi-1,*);
            	  v1= this.getCount(""+w2+w3+"*");
            	  //V(*,wi-2,wi-1)
            	  v2=this.getCount("*"+w2+w3+"*");	  
            	  
            	  if(v2 >0){
            	  p*= v1*d/v2;
            	  //V(*wi-2,wi-1,wi)
            	  v = this.getCount("*"+w2+w3+w4);	
            	  p+=Math.max(0, v-d)/v2;
            	  }
            	  
            	  
            	  //IKN 4-gram 
            	  v= this.getCount(""+w1+w2+w3+"*");
            	 
            	 subCount = this.getCount(""+w1+w2+w3);
            	 long subCountBi = this.getCount(""+w3+w4);
            	 long subCountTri = this.getCount(""+w2+w3+w4);
            	 
            	 if(subCount > 0){
            		 if( v>0){
            			 p*= (v*d) / subCount;
            		 }
            	  count = this.getCount(word);
            	  //check to reduce prob of palse positive.
            	  if((subCount > count) && (count<=subCountTri) && (subCountTri<=subCountBi) )
            		  p+= Math.max(0, count-d)/subCount;
            	 }
            	 
            	 break;
    	  }
    	  
    	  return p;
    	  
      }
      
        /**
         * default probability cal
         * @param word
         * @return
         */
        public double getConditionalProb(String word){
        	
        	return this.getIKNProb(word);
        }
        
      /**
       * Interpolated Smoothing
       */
        /*
      public double getJelinekProb(String word){
    	  int len = word.length();
  		int count = 0;
  		int subCount = 0;
  		double p=0, p1=0 ;
  		
  		if(len == 1){ 
  			 count =  this.getCount(word);
  			if( count!=0)
  				// p(w) = c(w)/(c(*));
  				return  (count*1.0)/ (this.TOKEN);
  			//unknown word encountered, need an appropriate method.
  			else return Double.MIN_VALUE;
  		}
  		
  		else if(len == 2){
  			count = this.getCount(word.substring(1, 2));
  			if(count !=0)
  				p1 = (count *1.0)/ (this.TOKEN); 
  			//unknown word (first character)
  			else p1 = Double.MIN_VALUE;
  			
  			count = this.getCount(word);
  			subCount =  this.getCount(word.substring(0, 1));
  			
  			// if subCount < count, that means 
  			//the mphr lookup gives a false positive to the count of 'word'
  			//so that 'word' is a OOV word.
  			//if(subCount > count) subCount = 0;
  			
  			if(count > 0 && subCount > count )
  				p = ALPHA*(count )/(subCount); //p(w2|w1)

  			
  				p +=  (1-ALPHA)*p1;
  		}else{
  			String history = word.substring(0,word.length()-1);
  			String sub = word.substring(1, word.length());
  			count =  this.getCount(word);
  			subCount = this.getCount(history);
  			
  			// if subCount < count, that means 
  			//the mphr lookup gives a false positive to the count of history word
  			//so that the history word is a OOV word.
  			//and if history word is OOV, the word itself is OOV as well: e.g. if 'w1w2' is an OOV, 'w1w2w3' must be an OOV as well.
 // 			if(subCount > count) subCount = 0;
  			
  			if(count > 0 && subCount > count)
  			    p = ALPHA* (count)/(subCount);
  		
  			p+= (1-ALPHA)*getConditionalProb(sub);			
  		}
  		return p;
      }
      */
   public static void main(String args[]){
	   
	   if(args.length != 5){
		   String info = "usage:"+MPHRLanguageModel.class.getName()+" IKN 0.94 "+" <MPHF object> <no. key bits> <no. value bits>  <bitSetfile> <rank/value file> ";
   			info +="\n\t: key+value bits must not exceed 64";
   		System.out.println(info);
   		System.exit(0);
	   }
	   
	   int numFingerprint = Integer.parseInt(args[1]);
	   int numRank = Integer.parseInt(args[2]);
	   MPHRLanguageModel mphrLM = new MPHRLanguageModel(numFingerprint,numRank);
	   System.out.println("loading mphr model: "+args[0]);
	   mphrLM.loadMPHF(args[0]);
	   
	   System.out.println("loading rank file: "+args[4]);
	   int noRank = mphrLM.loadRankFreqFile(args[4]);
	   System.out.println("Total:"+noRank+" unique ranks");
	   
	   System.out.println("loading data bitset: "+args[3]);
	   mphrLM.loadDataBitSet(args[3]);
	   
//	   mphrLM.loadDBConfig(args[5], DatabaseLanguageModel.CHINESEGIG);
	   
	   System.out.println("Total elements (types):"+mphrLM.numElement());
	   System.out.println("Total tokens:"+mphrLM.getTotalToken());
 //  String key = "●";
	   String key = "孺国武器";
	  	  
	   try{
		   System.out.println("type 'quit' to exit");
	  do {	   
		   System.out.print("please input key to search for:");
		   BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
		   key = br.readLine();
		   
 	       if(key.equalsIgnoreCase("quit") ){ System.exit(0);}
 	       if(key.length() ==0){ continue;}
 		 char c = key.charAt(key.length()-1);
	     String history = key.substring(0, key.length()-1);
	     
		   System.out.println("rank of \""+key+"\"="+mphrLM.rankOf(key));
		   System.out.println("freq of \""+key+"\"="+mphrLM.getCount(key));
		   //if the key contains '*', it is a context type count, so prob. estimation is not correct.
		   /*
		   if(!key.contains("*")){
			   System.out.println("freq of \""+key+"\"="+mphrLM.getCount(key));
			   System.out.println("p_ikn("+c+"|"+history+")="+mphrLM.getIKNProb(key));
		   }else{
			   System.out.println("context type of \""+key+"\"="+mphrLM.getCount(key));
		   }
		   */

	   }while(true);
	    
	   }catch(IOException ioe){
		   ioe.printStackTrace();
	   }
   
   }
}
