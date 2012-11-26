package org.apache.abdera2.examples.security;

import java.security.Key;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.abdera2.common.security.Otp;
import com.google.common.base.Supplier;

/**
 * Abdera includes a simple One-time-password generator
 * that can be used in a variety of scenarios to enhance 
 * security through two-factor authentication. 
 */
public class OtpExample {

  public static void main(String... args) throws Exception {
    
    // the time-based one-time-password uses time as the 
    // moving factor. so long as two parties have the same
    // source key and synchronize their clocks, they will
    // be able to generate the same one-time-password for 
    // the same window of time (in this example, the window
    // changes every 30 seconds). 
    
    // OTP generators are immutable and threadsafe.
    
    byte[] key = new byte[] {1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0};

    Otp.Totp totp = new Otp.Totp(30, key);

    System.out.println(totp.generateNext());
    
    
    // We can also use the Guava libraries supplier interface...
    
    Supplier<String> otp = Otp.totpSupplier(key, 30);
    System.out.println(otp.get());
    
    // Use your own OTP if you'd like...    
    otp = Otp.supplier(new MyOtp(key));
    System.out.println(otp.get()); // with this one,
    System.out.println(otp.get()); // the otp changes
    System.out.println(otp.get()); // every call because
    System.out.println(otp.get()); // of the counter
    
  }
  
  /** 
   * You can easily implement your own OTP based on 
   * other moving factors... such as this counter
   * based otp
   */
  public static class MyOtp extends Otp {

    public MyOtp(byte[] key, int size) {
      super(key, size);
    }

    public MyOtp(byte[] key, String alg, int size) {
      super(key, alg, size);
    }

    public MyOtp(byte[] key) {
      super(key);
    }

    public MyOtp(Key key, int size) {
      super(key, size);
    }

    public MyOtp(Key key, String alg, int size) {
      super(key, alg, size);
    }

    public MyOtp(Key key) {
      super(key);
    }

    public MyOtp(String key, int size) {
      super(key, size);
    }

    public MyOtp(String key, String alg, int size) {
      super(key, alg, size);
    }

    public MyOtp(String key) {
      super(key);
    }

    private final AtomicInteger counter = new AtomicInteger();
    
    @Override
    protected byte[] getMovingFactor() {
      int val = counter.incrementAndGet();
      return new byte[] {
          (byte)(val >>> 24),
          (byte)(val >>> 16),
          (byte)(val >>> 8),
          (byte)val};
    }
    
  }
  
}
