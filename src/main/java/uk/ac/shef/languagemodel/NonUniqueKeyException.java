package uk.ac.shef.languagemodel;
/**
 * Created by IntelliJ IDEA.
 * User: mpzarde
 * Date: Jun 29, 2006
 * Time: 5:13:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class NonUniqueKeyException extends Exception {
  /**
	 * 
	 */
	private static final long serialVersionUID = -9034141389208740271L;
String key;

  public NonUniqueKeyException(String s) {
    super("attempting to insert '" + s + "'");
    key = s;
  }

  public String getKey() {
    return (key);
  }
}
