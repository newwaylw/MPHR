package uk.ac.shef.trie;
/*
** Based on Koders - Trie.java
** http://www.koders.com/java/fid0F06E53F2CFCC6E591C38752F355A7178F92FFE5.aspx?s=trie
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**  17Jul06     mpzarde      modified to remove case sensitivity and finite alphabet, now uses maps to determine entries
**
*/

import java.util.*;

/**
 * The only thing that can't be stored in a
 * Trie safely is another Trie - we use
 * instanceof to determine if a Trie is
 * terminated or not
 */
public final class Trie {
// ------------------------------ FIELDS ------------------------------

  //  Information about the location of this Trie in the
  //  trie tree ;)
  int _position;
  Trie _previous;

  //  For when a string ends on this trie
  TrieEntry _entry;

  //  The contents of this trie
  Map _entries;

// --------------------------- CONSTRUCTORS ---------------------------

  /**
   * Creates a new trie with no entries
   */
  public Trie() {
    _entries = new HashMap();
    _entry = null;
    _position = 0;
    _previous = null;
  }

  //  Internal constructor
  private Trie(Trie parent) {
    this();
    _previous = parent;
    _position = parent.getPosition() + 1;
  }

  /**
   * Returns the character offset that this
   * trie is off the main trie
   */
  int getPosition() {
    return (_position);
  }

// --------------------- GETTER / SETTER METHODS ---------------------

  public Map getEntries() {
    return _entries;
  }

  public void setEntries(Map entries) {
    _entries = entries;
  }

  public TrieEntry getEntry() {
    return _entry;
  }

  public void setEntry(TrieEntry entry) {
    _entry = entry;
  }

  public Trie getPrevious() {
    return _previous;
  }

  public void setPrevious(Trie previous) {
    _previous = previous;
  }

// ------------------------ CANONICAL METHODS ------------------------

  public String toString() {
    if (length() > 20) {
      return ("Multiple matches: <too many to list (>20)>");
    } else {
      return ("Multiple matches: " + contents(new StringBuffer()));
    }
  }

  /**
   * Counts the number of linked objects off this trie
   */
  public int length() {
    int count = 0;

    if (getEntry() != null) {
      count++;
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof TrieEntry) {
          count++;
        } else {
          count += ((Trie) object).length();
        }
      }
    }


    return (count);
  }

  /**
   * Take current Trie and dump output to StringBuffer
   *
   * @param buffer
   * @return String holding content of current Trie   *
   */
  public String contents(StringBuffer buffer) {
    if (getEntry() != null) {
      buffer.append(getEntry().getKey());
      buffer.append(" ");
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();

        if (object instanceof Trie && (((Trie) object).getPrevious() == this)) {
          buffer.append(((Trie) object).contents());
        } else {
          buffer.append(((TrieEntry) object).getKey());
          buffer.append(" ");
        }
      }
    }
    return (buffer.toString());
  }

// -------------------------- OTHER METHODS --------------------------

  float averageClash() {
    TrieAverage trieAverage = new TrieAverage();
    averageClash(trieAverage);
    return (((float) trieAverage.getAverage()) / ((float) trieAverage.getNumber()));
  }

  void averageClash(TrieAverage average) {
    if (_entry != null) {
      average.add(_position);
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();

        if (object instanceof Trie && (((Trie) object)._previous == this)) {
          ((Trie) object).averageClash(average);
        } else {
          average.add(_position);
        }
      }
    }
  }

  public String contents() {
    return (contents(new StringBuffer()));
  }

  void dump() {
    String pre = spaces();
    if (getEntry() != null) {
      System.out.println(pre + getEntry().getKey());
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator keyIterator = getEntries().keySet().iterator();
      Iterator entryIterator = getEntries().values().iterator();

      while (keyIterator.hasNext() && entryIterator.hasNext()) {
        String key = (String) keyIterator.next();
        Object object = entryIterator.next();

        if (object instanceof Trie && (((Trie) object).getPrevious() == this)) {
          System.out.println(pre + "[" + key + "]");
          ((Trie) object).dump();
        } else {
          System.out.println(pre + ((TrieEntry) object).getKey());
        }
      }
    }
  }

  String spaces() {
    StringBuffer s = new StringBuffer();

    for (int index = 0; index < getPosition(); index++) {
      s.append(" ");
    }

    return (s.toString());
  }

  /**
   * Returns an alphabetically sorted list of
   * the elements in this trie.  This routine
   * is rather inefficient.
   */
  public Enumeration elements() {
    Vector entries = new Vector();
    elements(entries);
    return (entries.elements());
  }

  /**
   * Recursive function to append elements
   * to a linked list
   */
  protected void elements(Vector vector) {
    if (getEntry() != null) {
      vector.addElement(getEntry().getData());
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();

        if (object instanceof Trie && (((Trie) object)._previous == this)) {
          ((Trie) object).elements(vector);
        } else {
          vector.addElement(((TrieEntry) object).getData());
        }
      }
    }
  }

  /**
   * Returns an alphabetically sorted list of
   * the elements in this trie
   */
  public LinkedList getSortedList() {
    LinkedList ll = new LinkedList();

    listElements(ll);

    return (ll);
  }

  /**
   * Recursive function to append elements
   * to a linked list
   */
  protected void listElements(LinkedList ll) {
    if (getEntry() != null) {
      ll.addLast(getEntry().getData());
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();

        if (object instanceof Trie && (((Trie) object).getPrevious() == this)) {
          ((Trie) object).listElements(ll);
        } else {
          ll.addLast(((TrieEntry) object).getData());
        }
      }
    }
  }

  /**
   * Returns a Trie of all the elements starting with
   * 'key', or an Object which is a single exact match
   */
  public Object getTrieFor(String key) {
    if (key.length() == _position) {
      return (this);
    }

    String positionKey = getPositionKey(key);

    if (getEntries().containsKey(positionKey)) {
      Object object = getEntries().get(positionKey);

      if (object instanceof Trie) {    //  pass it along the chain
        return (((Trie) object).getTrieFor(key));
      } else {
        if (((TrieEntry) object).getKey().startsWith(key.toLowerCase())) {
          return (((TrieEntry) object).getData());
        }
      }
    }

    return (null);
  }

  private String getPositionKey(String key) {
    int startPos = _position;
    int endPos = (_position + 1 <= key.length() ? _position + 1 : _position);

    String positionKey = key.substring(startPos, endPos);
    return positionKey;
  }

  /**
   * Inserts the provided object into the trie
   * with the given key.
   */
  public void insert(String key, Object item) throws NonUniqueKeyException {
    if (key.length() == _position) {    //  this item needs to be inserted _on_ this trie
      if (_entry != null) {
        throw new NonUniqueKeyException(key);
      }
      _entry = new TrieEntry(key, item);
    } else {
      String positionKey = getPositionKey(key);

      if (getEntries().containsKey(positionKey)) {    //  already an item here
        Object object = getEntries().get(positionKey);
        if (object instanceof Trie && (((Trie) object)._previous == this)) {
          ((Trie) object).insert(key, item);
          return;
        } else {
          //  already something here - create a new
          //  trie, insert this something, and insert
          //  the new something
          TrieEntry entry = (TrieEntry) object;
          if (entry.getKey().equals(key)) {
            throw new NonUniqueKeyException(key);
          }

          Trie down = new Trie(this);
          down.insert(entry.getKey(), entry.getData());
          down.insert(key, item);

          getEntries().put(positionKey, down);
        }
      } else {    //  the space is empty - simply add it in
        getEntries().put(positionKey, new TrieEntry(key, item));
      }
    }
  }

  int numberTries() {
    int accum = 1;

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();

        if (object instanceof Trie && (((Trie) object)._previous == this)) {
          accum += ((Trie) object).numberTries();
        }
      }
    }

    return (accum);
  }

  void quickDump() {
    if (_entry != null) {
      System.out.print(_entry.getKey() + " ");
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();

        if (object instanceof Trie && (((Trie) object)._previous == this)) {
          ((Trie) object).quickDump();
        } else {
          System.out.print(((TrieEntry) object).getKey() + " ");
        }
      }
    }
  }

  /**
   * Removes the object with the given key from the
   * trie
   */
  public void remove(String key) throws NonUniqueKeyException, NoSuchElementException {
    if (key.length() == getPosition()) {
      //  item is on the '_entry' for this trie
      if (getEntry() != null) {
        setEntry(null);
        return;
      } else {
        throw new NonUniqueKeyException(key);
      }
    } else {
      String positionKey = getPositionKey(key);

      if (getEntries().containsKey(positionKey)) {
        Object object = getEntries().get(positionKey);


        if (object instanceof Trie && ((Trie) object).getPrevious() == this) {
          Trie trie = ((Trie) object);
          trie.remove(key);

          int subTrieSize = trie.length();
          if (subTrieSize == 1) {
            //  lower trie is now not required
            TrieEntry entry = trie.firstElement();
            if (entry != null) {
              getEntries().put(positionKey, entry);
            } else {
              getEntries().remove(positionKey);
            }
          }
        } else {
          TrieEntry entry = (TrieEntry) object;
          if (entry.getKey().equals(key)) {
            getEntries().remove(positionKey);
          }
          return;
        }
      } else {
        throw new java.util.NoSuchElementException(key);
      }
    }
  }

  /**
   * Returns the first element in a trie -
   * is generally only used when an element
   * is erased from the trie and a trie delete
   * and swapup is required.
   */
  TrieEntry firstElement() {
    if (_entry != null) {
      return _entry;
    }

    if (getEntries() != null && getEntries().size() > 0) {
      Iterator iterator = getEntries().values().iterator();

      while (iterator.hasNext()) {
        Object object = iterator.next();

        if (!(object instanceof Trie && (((Trie) object)._previous == this))) {
          return ((TrieEntry) object);
        }
      }
    }

    return (null);
  }

  /**
   * Returns a trie for multiple matches,
   * else returns null for no object matched,
   * or returns the object matched.  could
   * easily be optimised
   */
  public Object search(String key) {
    if (key.length() == getPosition()) {
      //  check _entry
      if (_entry != null) {
        //  this must be it
        return (_entry.getData());
      } else {
        //  multiple matches in this case
        return (this);
      }
    }

    String positionKey = getPositionKey(key);

    if (!getEntries().containsKey(positionKey)) {
      //  act as if this was the end of the
      //  string - return the _entry, if there is one
      if (_entry != null) {
        return (_entry.getData());
      } else {
        return (null);
      }
    } else {
      Object object = getEntries().get(positionKey);

      if (object instanceof Trie) {    //  pass it along the chain
        return (((Trie) object).search(key));
      } else {    //  found it (this check may not be required?)
        if (((TrieEntry) object).getKey().startsWith(key)) {
          return (((TrieEntry) object).getData());
        } else {
          return (null);
        }
      }
    }
  }

  /**
   * searchExact does *not* return a Trie for 'multiple matches',
   * but will return null in that case.
   */
  public Object searchExact(String key) {
    if (key.length() == _position) {    //  check _entry
      if (_entry != null) {    //  this must be it
        return (_entry.getData());
      } else {    //  multiple matches in this case
        return (null);
      }
    }

    String positionKey = getPositionKey(key);

    if (!getEntries().containsKey(positionKey)) {    //  act as if this was the end of the
      //  string - return the _entry, if there is one
      if (_entry != null) {
        return (_entry.getData());
      } else {
        return (null);
      }
    } else {
      Object object = getEntries().get(positionKey);

      if (object instanceof Trie) {    //  pass it along the chain
        return (((Trie) object).searchExact(key));
      } else {
        if ((_position + 1) < key.length()) {
          if (key.substring(0, _position + 1).equalsIgnoreCase(((TrieEntry) object).getKey())) {
            return (((TrieEntry) object).getData());
          }
        }

        if (key.equalsIgnoreCase(((TrieEntry) object).getKey())) {
          return (((TrieEntry) object).getData());
        } else {
          return (null);
        }
      }
    }
  }


}

