package uk.ac.shef.trie;
/**
 * Created by IntelliJ IDEA.
 * User: mpzarde
 * Date: Jun 29, 2006
 * Time: 5:13:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class NonUniqueKeyException extends Exception {
  String key;

  public NonUniqueKeyException(String s) {
    super("attempting to insert '" + s + "'");
    key = s;
  }

  public String getKey() {
    return (key);
  }
}
