package uk.ac.shef.trie;

import java.io.Serializable;

/**
 * A private class which is used to
 * internally mark off elements to
 * items
 */
class TrieEntry implements Serializable {
  private String _key;
  private Object _data = null;

  public TrieEntry() {
    this("", null);
  }

  public TrieEntry(String key, Object data) {
    setKey(key);
    setData(data);
  }

  public String getKey() {
    return _key;
  }

  public void setKey(String key) {
    _key = key;
  }

  public Object getData() {
    return _data;
  }

  public void setData(Object data) {
    _data = data;
  }

}
