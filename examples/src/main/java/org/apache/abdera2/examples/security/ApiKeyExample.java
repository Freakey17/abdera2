package org.apache.abdera2.examples.security;

import org.apache.abdera2.common.security.ApiKey;

import com.google.common.base.Supplier;

public class ApiKeyExample {

  public static void main(String... args) throws Exception {
    
    // Many REST-apis deployed today employ random, cryptographically
    // generated API Keys used for keeping track of api calls and 
    // monitoring quotas, etc. Abdera includes a simple Api Key 
    // generator that can be used to generate several variations of
    // random strings usable as API Keys. The generated values are
    // cryptographically derived from a given key and can vary in 
    // size. 
    
    byte[] key = new byte[] {1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0};

    ApiKey apikey = ApiKey.STRONG(key);
    
    System.out.println(apikey.generateNext());
    
    // You can use the Guava Library Supplier interface also...
    
    Supplier<String> sup = apikey.asSupplier();
    System.out.println(sup.get()); // every call generates a new 
    System.out.println(sup.get()); // random api key string
    
    // instances of the ApiKey and the supplier are immutable and
    // threadsafe..
    
    // the generated api keys are guaranteed to only contain characters
    // that are safe for use within URL's, so they can easily be passed
    // around in URL Query String parameters as is typical of most API
    // key usage scenarios
    
    
  }
  
}
