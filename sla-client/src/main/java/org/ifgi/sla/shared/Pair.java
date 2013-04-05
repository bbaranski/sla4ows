package org.ifgi.sla.shared;

import java.io.Serializable;


public class Pair implements Serializable {

	private static final long serialVersionUID = -4609717041085613809L;

protected  String Key_;
  protected  String Value_;

  
  public Pair() {
	  Key_=null;
	  Value_=null;
	  }
  public Pair(String key, String value) {
    Key_   = key;
    Value_ = value;
  }
  public String getKey() {
    return Key_;
  }
  public String getValue() {
    return Value_;
  }
  public String toString() {
    System.out.println("in toString()");
    StringBuffer buff = new StringBuffer();
      buff.append("Key: ");
      buff.append(Key_);
      buff.append("\tValue: ");
      buff.append(Value_);
    return(buff.toString() );
  }
 
  public boolean equals( Pair p1 ) { 
    System.out.println("in equals()");
    if ( null != p1 ) { 
      if ( p1.Key_.equals( this.Key_ ) && p1.Value_.equals( this.Value_ ) ) { 
        return(true);
      }
    }
    return(false);
  }
  
}
