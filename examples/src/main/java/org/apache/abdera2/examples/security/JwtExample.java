package org.apache.abdera2.examples.security;

import java.security.KeyPair;

import org.apache.abdera2.activities.extra.Jwt;
import org.apache.abdera2.activities.model.Activity;
import org.apache.abdera2.activities.model.Verb;
import org.apache.abdera2.common.security.KeyHelper;

/**
 * The Abdera Activity Streams implementation includes
 * basic support for the JSON Web Tokens (JWT) spec.
 * This essentially gives us digital signatures for 
 * Activities / JSON. The mechanism can generate and 
 * validate JWT's using a supplier key pair
 */
public class JwtExample {

  public static void main(String... args) throws Exception {
   
    KeyHelper.prepareDefaultJceProvider();
    
    KeyPair pair = KeyHelper.generateKeyPair("RSA", 1024);

    Activity activity = 
      Activity
        .makeActivity()
          .verb(Verb.POST)
            .get();

    String jwt = Jwt.generate(pair.getPrivate(), activity);
    
    System.out.println(jwt);

    activity = (Activity) Jwt.getClaimIfValid(pair.getPrivate(), jwt);  
    
    System.out.println(activity);
    
  }
  
}
