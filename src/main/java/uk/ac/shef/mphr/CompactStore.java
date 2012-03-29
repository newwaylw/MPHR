package uk.ac.shef.mphr;

import uk.ac.shef.util.MurmurHash;

public class CompactStore {
	public static final int HASH_SEED = 1;
	private final int finger_print_size; //number of bits to hold fingerprint
	private final int value_size;  //number of bits to hold value
	//private final int valuemask;
	private final long valuemask;
	//private final int fingerprintmask;
	private final long fingerprintmask;
	//	private LongBitSet bitSet;  //the bit array
//	private long totalNumberOfBits;  //number of bits in bit array
	
	
	public CompactStore (int bits_per_fingerprint, int bits_per_value){
		this.finger_print_size = bits_per_fingerprint ;
		this.value_size = bits_per_value;
		this.valuemask = ((long)Math.pow(2, bits_per_value))-1;
		this.fingerprintmask = (long)Math.pow(2,bits_per_fingerprint)-1;
		
	}
	  /**
	    * random hash function for fingerprints.
	    * since Murmurhash returns an integer, the key
	    * must be represented by a hash value of less than 32bits.
	    * @param key
	    * @return in integer hash value for this key.
	    */
	   public int fp(String key){
	       int hash = MurmurHash.hash(key.getBytes(), HASH_SEED);
	       hash&=this.fingerprintmask;       
	       return hash;
	   }
	   
	   /*
	   private int cleanVal(int value){
			value&=valuemask;
			return value;
		}
	   */
	   
	   /**
	    * value is a (positive) integer (31 bits max)
	    * @param key - key to be hash 
	    * @param value - value to be hash with
	    * @return a combined fingerprint+value long representation
	    *  		  value will not be greater than 2^bitsPerElement
	    */
	   public long encode(String key,long value){
			//long fpv=fp(key);
			//fpv|=cleanVal(value);
		   if(value > Math.pow(2, this.value_size) ){
			   System.err.println("value:"+value +" exceeds the bits representable with "+this.value_size+" bits.");
			   System.exit(-1);
		   }
		   long fpv = this.fp(key);
		   //make room for value bits
		   fpv = fpv << this.value_size;
		   fpv |= value;
			return fpv;
		}
}
