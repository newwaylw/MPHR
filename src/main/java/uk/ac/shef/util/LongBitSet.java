package uk.ac.shef.util;

import java.util.BitSet;

/**
 * A LongBitSet that can hold more bits than java.util.BitSet, 
 * which can only hold up to Integer.MAX_VALUE bits
 * Uses an array of BitSets to manipulate.
 * 
 * @author Wei Liu @ Univ. of Sheffield
 * @since 2009 
 *
 */
public class LongBitSet extends BitSet{
	private static final long serialVersionUID = -8645815627471826484L;

	private  BitSet[] bitSet;
	private int blockSize = Integer.MAX_VALUE;
	private int block;
	private int remainder;
	
	/**
	 * use multiple bitSet(int) to represent a Long bitset;
	 * Creates a bit set whose initial size is large enough to explicitly 
	 * represent bits with indices in the range 0 through bitSize-1.
	 * @param bitSize - number of bits to allocate.
	 */
	public LongBitSet(long bitSize){
		this.block = (int)(bitSize / blockSize);
		this.remainder = (int)(bitSize % blockSize);
		//if the bitset size is less or equal to blockSize
		//then we just need 1 block
		if(this.block==0 || (this.block ==1 &this.remainder==0)){
			bitSet = new BitSet[1];
			bitSet[0] = new BitSet((int) bitSize);
		}else if(this.remainder==0){
			bitSet = new BitSet[this.block];
			for(int i=0;i<block;i++)
				bitSet[i] = new BitSet(blockSize);
		}else{
			bitSet = new BitSet[block+1];
			for(int i=0;i<block;i++)
				bitSet[i] = new BitSet(blockSize);
			bitSet[block] = new BitSet(remainder);
		}
	}
	
		/**
		 * return the allocated size (number of bits) of this LongBitSet;
		 */
		public long capacity(){
			long size= block;
			size *= blockSize;
			size += remainder;
			return size;
		}
		
		/**
		 * range check for two indexes
		 * @param fromIndex
		 * @param toIndex
		 */
		private void rangeCheck(long fromIndex, long toIndex){
			if(fromIndex<0)
				throw new IndexOutOfBoundsException(": fromIndex < 0: "+fromIndex);
			if(toIndex<0)
				throw new IndexOutOfBoundsException(": toIndex < 0: "+toIndex);
			if(toIndex < fromIndex)
				throw new IndexOutOfBoundsException(": fromIndex: "+fromIndex+" > toIndex: "+toIndex);
			if(toIndex >= this.capacity())
				throw new IndexOutOfBoundsException(": toIndex: "+toIndex+" > size: "+this.capacity());
			
		}
		
		/**
		 * get a bit in specified position 
		 * @param bitIndex
		 * @return value of this bit
		 */
		public boolean get(long bitIndex){
			int block = (int)(bitIndex / blockSize);
			int remainder = (int)(bitIndex % blockSize);
			return this.bitSet[block].get(remainder);
	}
		
		/**
		 * get a bit in specified position 
		 * @param bitIndex
		 * @return
		 */
		public void set(long bitIndex){
			int block = (int)(bitIndex / blockSize);
			int remainder = (int)(bitIndex % blockSize);
			this.bitSet[block].set(remainder);
	}
		
		/**
		 * set a bit to a specified value
		 * @param bitIndex
		 * @param value
		 */
		public void set(long bitIndex, boolean value){
			int block = (int)(bitIndex / blockSize);
			int remainder = (int)(bitIndex % blockSize);
			this.bitSet[block].set(remainder,value);
		}
		
		/**
		 * Sets the bits from the specified fromIndex (inclusive) to the specified
		 *  toIndex (exclusive) to true.
		 * @param fromIndex
		 * @param toIndex
		 */
		public void set(long fromIndex,long toIndex){
			//argument range check.
			rangeCheck(fromIndex,toIndex);
			
			int fBlock = (int)(fromIndex / blockSize);
			int fRemainder = (int)(fromIndex % blockSize);
			int tBlock = (int)(toIndex / blockSize);
			int tRemainder = (int)(toIndex % blockSize);
			
			//same block
			if(tBlock == fBlock){
				bitSet[fBlock].set(fRemainder, tRemainder);
			}else{
				//it crosses block boundaries

				//set the first block till the end
				bitSet[fBlock].set(fRemainder,blockSize);
				//set the blocks it completely crosses.
				for(int i=fBlock+1;i<tBlock;i++)
					bitSet[i].set(0, blockSize);
				
				//set the rest
				if(tRemainder!=0)
					bitSet[tBlock].set(0,tRemainder);			
			}			

		}
		
		/**
		 * set bits from offset to match the 
		 * value of given integer representation.
		 * if 2^(mask_len)-1 < value, this method will
		 * yield incorrect value. 
		 * @param offset - position in the bit array offset.
		 * @param mask_len - length (bits) used as mask
		 * @param value - the value (integer) needs to be set
		 */
		public void setValue(long offset, int mask_len, int value){
			final int MASK=(int)Math.pow(2,mask_len-1);
			//long offset=pos*this.bitsPerElement;
			for (int i=0; i<mask_len; i++) {
				this.set(offset+i,(value&MASK)>0?true:false);
				value<<=1; //shift value left by one
			}
		}
		/**
		 * Returns a new LongBitSet composed of bits from 
		 * this LongBitSet  from fromIndex (inclusive) to toIndex (exclusive).
		 * @param fromIndex
		 * @param toIndex
		 * @return
		 */
		public LongBitSet get(long fromIndex, long toIndex){
			rangeCheck(fromIndex,toIndex);
			long len = toIndex - fromIndex;
			LongBitSet localLongBitSet = new LongBitSet(len);
			
			for(long i=0;i<len;i++)
				localLongBitSet.set(i,this.get(fromIndex+i));
			
			return localLongBitSet;
		}
		
		/**
		 * Sets all of the bits in this BitSet to false.
		 */
		public void clear(){
			for(int i=0; i<bitSet.length;i++)
				bitSet[i].clear();
		}
		
		public void clear(long bitIndex) {
			int block = (int)(bitIndex / blockSize);
			int remainder = (int)(bitIndex % blockSize);			
			bitSet[block].clear(remainder);
		}
		/**
		 * 
          *Returns true if this BitSet contains no bits that are set to true.
		 */
		public boolean isEmpty(){
			for(int i=0; i<bitSet.length;i++)
					if(!bitSet[i].isEmpty()) return false;
			return true;
		}
		
		/**
		 * Sets the bit at the specified index to the complement of its current value.
		 * @param index
		 */
		public void flip(long bitIndex){
			int block = (int)(bitIndex / blockSize);
			int remainder = (int)(bitIndex % blockSize);			
			this.bitSet[block].flip(remainder);
		}
		
		/**
		 * Sets each bit from the specified fromIndex (inclusive) to the specified toIndex (exclusive) 
		 * to the complement of its current value.
		 * @param fromIndex
		 * @param toIndex
		 */
		public void flip(long fromIndex,long toIndex){
			int fBlock = (int)(fromIndex / blockSize);
			int fRemainder = (int)(fromIndex % blockSize);
			int tBlock = (int)(toIndex / blockSize);
			int tRemainder = (int)(toIndex % blockSize);
			
			//same block
			if(tBlock == fBlock){
				bitSet[fBlock].flip(fRemainder, tRemainder);
			}else{
				//it crosses block boundaries
				//set the first block till the end
				bitSet[fBlock].flip(fRemainder,blockSize);
				//set the blocks it completely crosses.
				for(int i=fBlock+1;i<tBlock;i++)
					bitSet[i].flip(0, blockSize);
			}			
				//set the rest
				bitSet[tBlock].flip(0,tRemainder);			
		}
		
		public long nextSetBit(long fromIndex){
			int block = (int)(fromIndex / blockSize);
			int remainder =  (int)(fromIndex % blockSize);
			
			//next is always > remainder in the same block.
			int next = bitSet[block].nextSetBit(remainder);
			//if found, return the new index
			if(next != -1) return block * blockSize+next;
			//keep looking further blocks
			for(int i=block+1;i<bitSet.length;i++){
				next = bitSet[i].nextSetBit(0);
				if(next == -1) continue;
				return block*blockSize + next;
			}
			
			next = bitSet[bitSet.length-1].nextSetBit(0);
			if(next != -1) return bitSet.length * blockSize + next;
			else return -1;				
		}	
		
		/**
		 * the number of blocks used
		 * @return
		 */
		/*
		public int numBlocks(){
			return this.block;
		}
		*/
		/**
		 * the size of each block, default is Integer.MAX_VALUE
		 * @return
		 */
		/*
		public int getBlockSize(){
			return this.blockSize;
		}
		*/
		/**
		 * remainder of the long bitset.
		 * @return
		 */
		/*
		public int getRemainder(){
			return this.remainder;
		}
		*/

		
		/**
		 * return indexes of set bits.
		 */
		public String toString(){
			StringBuffer buf = new StringBuffer("{");
			for(int j=0;j<bitSet.length;j++){
				for (int i = bitSet[j].nextSetBit(0); i >= 0; i = bitSet[j].nextSetBit(i+1)) 
					buf.append(i+",");					
			}
			buf.deleteCharAt(buf.length()-1);
			buf.append("}");
			return buf.toString();
		}
		
		public boolean equals(Object o){
			//check for self-comparison
		    if ( this == o ) return true;

		    //use instanceof instead of getClass here for two reasons
		    //1. if need be, it can match any supertype, and not just one class;
		    //2. it renders an explict check for "that == null" redundant, since
		    //it does the check for null already - "null instanceof [type]" always
		    //returns false. (See Effective Java by Joshua Bloch.)
		    if ( !(o instanceof LongBitSet) ) return false;
		    //Alternative to the above line :
		    //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

		    //cast to native object is now safe
		    LongBitSet set2 = (LongBitSet)o;    
		    //go through each block and see if every set bits in this set is also set in the other
		    for(int j=0;j<this.bitSet.length;j++){
				for (int i = this.bitSet[j].nextSetBit(0); i >= 0; i = bitSet[j].nextSetBit(i+1)){
					if(!set2.get(j*blockSize+i) ) return false;
				}
		}
		    return true;
	}
	
    /**
     * Returns the number of bits set to true in this BitSet.
     */
	public int cardinality() {
		int cardin = 0;
		for(int i=0; i<bitSet.length;i++){
			cardin+=bitSet[i].cardinality();
		}
		return cardin;
	}
		
		public static void main(String[] args){
/*			BitSet set1 = new BitSet(Integer.MAX_VALUE);
			BitSet set2 = new BitSet(50);
			set1.set(1);
			set1.set(3);
			
			set2.set(1);
			set2.set(2);
			set1.or(set2);			
			System.out.println("two set equal ="+set1.equals(set2));
			System.out.println(set2.get(0,10));
 * 
 */
			System.gc();
			long size = 8240000000L;
			LongBitSet setl = new LongBitSet(size);
			
			long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			double per = (m1*8.0) /size ;
			m1 /= 1024*1024;
			System.out.println("memory used:"+m1+" MBytes.");
			System.out.println("each bit costs:"+per+ " bits");

			int loop=0;
			while(loop<Long.MAX_VALUE){
				loop++;
				long m =(long)(Math.random()*size);
//				if(m>Integer.MAX_VALUE)
//					System.out.println("m="+m);
				setl.set(m);
				if(!setl.get(m)){System.out.println("bit error at: "+m);}
//				long n =  (long)(Math.random()*size);
//				long from = Math.min(n,m);
//				long to = Math.max(m, n);				
//				from = 1000;
//				to = 1023;
//				long len = to - from;
				//to is exclusive...
//				setl.set(from,to);
//				long c=0;
				
//				for(c=0;c<len;c++){
//					if(!setl.get(from+c)){
//						System.out.println("bit:"+(from+c)+" error!");
		
//					System.exit(-1);
//					}
//				}
				//if(!setl.get(m)) System.out.println("m:"+m+" error!");
		  }	
				
//			
		}
}
