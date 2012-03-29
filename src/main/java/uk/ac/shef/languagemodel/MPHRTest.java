package uk.ac.shef.languagemodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class MPHRTest {

	
	   public static void main(String args[]){
		   
		   if(args.length != 6){
			   String info = "usage:"+MPHRTest.class.getName()+"0.94 "+" <MPHF object> <no. key bits> <no. value bits>  <bitSetfile> <rank/value file> <num random keys>";
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
		   
//		   mphrLM.loadDBConfig(args[5], DatabaseLanguageModel.CHINESEGIG);
		   
		   System.out.println("Total elements (types):"+mphrLM.numElement());
		   
		   int numKeys = Integer.parseInt(args[5]);
		   Random random = new Random();
		   int value = 0;
		   String randomKey ;
		   int fp=0;
		   long t1 = System.currentTimeMillis();
		   
		   for(int n=0;n<numKeys;n++){
			   randomKey = ""+random.nextLong();
			    // value = (int)(Math.random()*10000000) + 40;			   
			   //false positive
			   if(mphrLM.getCount(randomKey) > 0){fp++;}
			   
		   }
		   long t2 = System.currentTimeMillis();
		   
		   long spent = (t2-t1)/1000;
		   System.out.println(numKeys + "random unseen keys tested in"+spent+"seconds, "+fp+" false positives.");
		   double fpRate = (fp * 1.0 ) / numKeys * 100.0;
		   System.out.println("fp-rate = "+fpRate);
//		   System.out.println("Total tokens:"+mphrLM.getTotalToken());
		  	/*  
		   try{
			   File file = new File(args[5]);
			   String[] files;
			   if(file.isDirectory()){
				   files = file.list();
				   for(int i=0;i<files.length;i++){
					   files[i] = args[5]+File.separatorChar+files[i];
				   }
				   
			   }else{
				   //single file
				   files = new String[1];
				   files[0] = args[0];
			   }
			   //String filename;
			   for( String filename : files){
				   System.out.println("Reading:"+filename);
				   BufferedReader br = new BufferedReader(new FileReader(filename) );
				   String cnLine;
				   String key;
				   long value;
				   long valueAtFile;
				   while((cnLine=br.readLine())!=null){
					String[] parts = cnLine.split("\\t");
					key = parts[0];
					valueAtFile = Long.parseLong(parts[1]);
//				   System.out.println("rank of \""+key+"\"="+mphrLM.rankOf(key));
					value = mphrLM.getCount(key);
				  //System.out.println("freq of \""+key+"\"="+value);
				   
				   
				   if(value == valueAtFile ){
					   System.out.println("freq of ["+key+"]="+value + "\t Equal!");
				   }
				   else if(value==0){
					   System.out.println("freq of ["+key+"]="+value + "\t Key does not exist!");
				   }
				   else{
					   System.out.println("freq of ["+key+"]="+value + ";Acutal Count="+valueAtFile+" Not Equal!");
				   }			  
				  }
			   }
			   }catch(IOException ioe){
				   System.err.println();
				   ioe.printStackTrace();
			   }
	   	*/
		   
	   }	
	
}
