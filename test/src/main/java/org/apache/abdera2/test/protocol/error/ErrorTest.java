package org.apache.abdera2.test.protocol.error;

import org.apache.abdera2.Abdera;
import org.apache.abdera2.protocol.error.Error;
import org.apache.abdera2.protocol.error.AbderaProtocolException;
import org.junit.Test;

public class ErrorTest {

  @Test(expected=AbderaProtocolException.class)
  public void errorTest() {
    Abdera abdera = Abdera.getInstance();
    Error error = Error.create(abdera,10,"foo");
    AbderaProtocolException pe = new AbderaProtocolException(error);
    throw pe;
  }
  
}
