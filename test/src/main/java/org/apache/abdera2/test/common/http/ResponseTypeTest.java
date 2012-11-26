package org.apache.abdera2.test.common.http;

import static org.junit.Assert.assertEquals;

import org.apache.abdera2.common.http.ResponseType;
import org.junit.Test;

public class ResponseTypeTest {

  @Test
  public void responseTypeTest() {
    assertEquals(ResponseType.SUCCESSFUL, ResponseType.select(201));
    assertEquals(ResponseType.REDIRECTION, ResponseType.select(301));
    assertEquals(ResponseType.CLIENT_ERROR, ResponseType.select(401));
    assertEquals(ResponseType.SERVER_ERROR, ResponseType.select(501));
    assertEquals(ResponseType.UNKNOWN, ResponseType.select(601));
    assertEquals(ResponseType.INFORMATIONAL, ResponseType.select(101));
  }
  
}
