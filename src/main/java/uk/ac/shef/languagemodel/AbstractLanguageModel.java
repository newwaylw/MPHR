package uk.ac.shef.languagemodel;

/**
 * 
 * Abstract LanguageModel 
 * @author wei
 * @since 2010
 */
public abstract class AbstractLanguageModel {

	public  AbstractLanguageModel(){
		
	}
	
	/**
	 * Counts of this String.
	 * @param word
	 * @return how many times word occurred 
	 */
	public abstract  long getCount(String word) ;
	
	/**
	 * return the conditional probability of this string (depends on smoothing method). 
	 * e.g. prob("ABCDE") = p(E|ABCD);
	 *      prob("XYZ") = p(Z|XY);
	 * @param word
	 * @return
	 */
	public abstract double getConditionalProb(String word);
	
}
