package org.apache.abdera2.examples.activities;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import javax.activation.DataHandler;

import org.apache.abdera2.activities.model.IO;
import org.apache.abdera2.activities.model.objects.BadgeObject;
import org.apache.abdera2.activities.model.objects.BinaryObject;

import static org.apache.abdera2.common.io.Compression.CompressionCodec.DEFLATE;
import static org.apache.abdera2.common.security.HashHelper.Md5;
import static org.apache.abdera2.activities.model.objects.BadgeObject.makeBadge;
import static org.apache.abdera2.activities.model.objects.BinaryObject.makeBinary;

/**
 * Illustrates the extension "binary" objectType... this can be useful, 
 * for instance, when attaching arbitrary base64 encoded data to an 
 * object (e.g in the "attachments" property)
 */
public class BinaryDataObjectExample {

  private final static IO io = IO.get();
  
  public static void main(String... args) throws Exception {
    
    // First prepare the data we wish to include
    URL url = BinaryDataObjectExample.class.getResource("/info.png");
    DataHandler dataHandler = new DataHandler(url); 
    
    // Let's create a badge object and attach the data object...
    BadgeObject badge = 
      makeBadge()
        .attachment(
          makeBinary()
            .data(
              dataHandler, // the data will be automatically base64 encoded
              new Md5(),   // generate an md5 hash and set an "md5" property.. we could also do an hmac here
              DEFLATE)     // apply deflate compression to the data before encoding it
            .get())
        .get();
    
    // check the round trip //
    StringReader sr = new StringReader(io.write(badge));
    
    badge = io.readObject(sr);
 
    // grab the binary object from the attachments...
    BinaryObject dataObject = (BinaryObject) badge.getAttachments().iterator().next();
    
    String md5 = dataObject.getProperty("md5");
    Md5 check = new Md5();
    
    // decompression and base64 decoding will be applied automatically
    // when we read the inputstream provided by getInputStream()...
    InputStream in = dataObject.getInputStream(); 
    byte[] buf = new byte[1024];
    int r = -1;
    while((r = in.read(buf)) > -1)
      check.update(buf, 0, r);
    
    // check the md5 hash to show that the input data and output data are the same
    check.checkElement(md5);
    
  }
  
}
