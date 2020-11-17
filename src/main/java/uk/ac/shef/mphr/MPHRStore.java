package uk.ac.shef.mphr;

import uk.ac.shef.util.*;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.sux4j.io.ChunkedHashStore;
import it.unimi.dsi.sux4j.mph.MinimalPerfectHashFunction;
import it.unimi.dsi.bits.TransformationStrategies;

/**
* implementation of Minimum Perfect Hashing Rank language model storage
* @author Wei Liu
* @since Feb 2010
*
*/
public class MPHRStore {
   private int bitsFingerprint; //number of bits used as fingerprint;
   private  int bitsValue;        //number of bits used as rank, 
   CompactStore fpStore;
   private LongBitSet dataBitSet;
   //an long list to hold counts for each rank.
   private ArrayList<Long> rankList;
   private MinimalPerfectHashFunction<String> mphf ;
   static Logger logger = LogManager.getLogger(MPHRStore.class);

   /**
    * Constructor, allocate the bit array 
    * @param bitsPerFingerprint - use long so that it can store more than Integer.MAX_VALUE .
    * @param bitsPerRank
    * @param numElement 
    */
   public MPHRStore(int bitsPerFingerprint, int bitsPerRank, long numElement){
//	   PropertyConfigurator.configure(Config.LOG4J_CONF);
       this.bitsFingerprint = bitsPerFingerprint;
       this.bitsValue = bitsPerRank;
       this.rankList = new ArrayList<Long>();
       fpStore =  new CompactStore(this.bitsFingerprint,this.bitsValue);
       long payloadBits = this.bitsFingerprint + this.bitsValue; //fingerprint(key) bits + value (freq) bits
       payloadBits *= numElement; // total number of elements needs to be stored
       this.dataBitSet = new LongBitSet(payloadBits);  
    }
   
   /**
    * loads a pre-generated mphr function 
    * 
    * @param mphf
    */
   public void loadMPHF (String mphf){
   	   Object o = Util.readObject(mphf);
       if(o instanceof MinimalPerfectHashFunction<?>){
           this.mphf = (MinimalPerfectHashFunction<String>) o;
       }else{
           System.err.println(mphf+" is not a object of MinimalPerfectHashFunction! Please check.");
           System.exit(-1);
       }
          }
   
   	 /**
   	  * MPHR store the N-gram language Model,
   	  * unique frequencies are stored in an arrayList and export as text file and serialised object.
   	  * 
   	  */
      public void store(String dataFile){
    	  try{
    		  BufferedReader reader;
    	 		 if(!dataFile.endsWith(".gz"))
    	 			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "UTF8"));
    	 	  	  else
    	 	  		reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(dataFile)),"UTF8"));
    	    String line;
  	    
    	    int rank = -1;     	    
    	    long no = 0;
    	   
    	    String keyStr;
    	    String[] keys;
    	   long freq =0;
    	    long v = 0;
    	    //combined bit size of fingerprint and value;
	    	int dataLength = this.bitsFingerprint+this.bitsValue;
	    	//a mask for the highest bit (1)
			final long mask=(long)Math.pow(2,dataLength-1);
			
			 // Now read lines of text
    	    while ((line = reader.readLine()) != null){

    	    	//n g r m	freq
    	    	keys = line.split("\\t");
    	    	if(keys.length != 2){ continue;}
    	    	no++;
    	    	//the input file must be sorted
    	    	//once the value read is different from the previous value
    	    	//we know we've reached the next distinct value.
    	    	
    	    	//use long to cope with huge numbers 
    	    	if(Long.parseLong(keys[1]) != freq){ 
    	    		rank++;
    	    		freq = Long.parseLong(keys[1]);
    	    		rankList.add(freq);
    	    	}
    	    	keyStr = keys[0];
    	    	long payload = fpStore.encode(keyStr, rank);
    	    	long offset = this.mphf.getLong(keyStr);
    	    	 //offset = 1525503
    	    	offset *= dataLength;
    			//long offset=pos*this.bitsPerElement;
    			for (int i=0; i<dataLength; i++) {
    				
    				v = payload&mask;
    				//set one bit a time with masking, start from the highest bit
    				this.dataBitSet.set(offset+i,v!=0?true:false);
    				payload<<=1; //shift value left by one
    			}
    			
    			logger.debug(no+" lines processed.");
    			
    	    }
    
    	  }catch(IOException ioe){
    		  ioe.printStackTrace();
    	  }
      }
      
      /**
       * serialize the bit set
       * @param outputFile file name to write to.
       */
      public void writeBitSet(String outputFile){
		  logger.debug("number of true bits:"+this.dataBitSet.cardinality());
    	  Util.serializeObject(this.dataBitSet, outputFile);
      }
      
      /**
       * serialize the rank list.
       * @param outputRankListFile
       */
      public void writeRankList(String outputRankListFile){
    	  Util.serializeObject(this.rankList, outputRankListFile);
      }
      
      /**
       * write the unique frequencies out as text file.
       * @param outputRankListFile
       */
      public void writeRankListAsTxtFile(String outputRankListFile){
    	  try{
    	  BufferedWriter writer = new BufferedWriter(new FileWriter(outputRankListFile));
    	  	for(long freq : this.rankList){
    	  		writer.write(freq+"\n");
    	  	}
    	  	writer.close();
    	  }catch(IOException ioe){
    		  ioe.printStackTrace();
    	  }
      }
      
      
      public static void main(String[] args){
    	  
    	if(args.length != 6){
    		String info = "usage:"+MPHRStore.class.getName()+" ver.0.99a "+" <MPH function object> <no. key bits> <no. value bits> <total element> <ngram  file> <outputbitsetfile_basename>";
    		info +="\n\t: key+value bits must not exceed 64";
    		System.out.println(info);
    		System.exit(0);
    	}

    	 String mphObjFile = args[0];
    	 int noKeyBits = Integer.parseInt(args[1]);
    	 int noValueBits = Integer.parseInt(args[2]);
    	 //less than 2^31 elements
    	 int noElements = Integer.parseInt(args[3]);
    	 String datafile = args[4];
    	 String outputFile = args[5];
    	  MPHRStore mphrStore = new MPHRStore (noKeyBits, noValueBits, noElements);
    	 // System.err.print();
    	  logger.info("Load MPHF object:"+mphObjFile);
    	  mphrStore.loadMPHF(mphObjFile);
    	  
    	  logger.info("Load data file:"+datafile);
    	  mphrStore.store(datafile);
    	  
    	  logger.info("writing bit array file:"+outputFile+".bitset");
    	  mphrStore.writeBitSet(outputFile+".bitset");
    	  
    	  logger.info("writing rank object serialisation:"+outputFile+".rank.obj");
    	  mphrStore.writeRankList(outputFile+".rank.obj");
    	  
    	  logger.info("writing bit rank object file:"+outputFile+".rank.txt");
    	  logger.info("there are:"+mphrStore.rankList.size()+" unique ranks.");
    	  mphrStore.writeRankListAsTxtFile(outputFile+".rank.txt");
	  
    	  
    	  //        long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//        m1 /= 1024;
//        System.out.println("memory used:"+m1+" KBytes.");
//       Util.serializeObject(mphf, "mphf.obj");

   }
}

