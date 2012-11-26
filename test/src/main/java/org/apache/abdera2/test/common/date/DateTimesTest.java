package org.apache.abdera2.test.common.date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.apache.abdera2.common.date.DateTimes.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class DateTimesTest {

  @Test
  public void testDateTimes() {
    
    DateTime dt = now();
    assertNotNull(dt);
    
    dt = utc("2012-12-12T12:12:12-00:00");
    assertEquals(2012,dt.getYear());
    assertEquals(12,dt.getMonthOfYear());
    assertEquals(12,dt.getDayOfMonth());
    assertEquals(12,dt.getHourOfDay());
    assertEquals(12,dt.getMinuteOfHour());
    assertEquals(12,dt.getSecondOfMinute());
    assertEquals(DateTimeZone.UTC, dt.getZone());
    
    assertEquals("2012-12-12T12:12:12.000Z", format(dt));
    
    // not testing every combination here.. just making sure basic mechanism works...
    
    assertTrue(after(dt("2012-12-12T12:12:11-00:00")).apply(dt));
    assertFalse(afterBetweenOrAt(dt("2012-12-12T12:12:12-00:00"),dt("2012-12-12T12:12:14-00:00")).apply(dt));
    assertTrue(afterBetweenOrAt(dt("2012-12-12T12:12:11-00:00"),dt("2012-12-12T12:12:13-00:00")).apply(dt));
    assertTrue(afterBetweenOrAt(dt("2012-12-12T12:12:10-00:00"),dt("2012-12-12T12:12:12-00:00")).apply(dt));
    
    assertTrue(afterNow().apply(now().plusMinutes(1)));
    assertFalse(afterNow().apply(now().minusMinutes(1)));
    
    assertTrue(atBetweenOrBefore(dt("2012-12-12T12:12:12-00:00"),dt("2012-12-12T12:12:14-00:00")).apply(dt));
    assertTrue(atBetweenOrBefore(dt("2012-12-12T12:12:11-00:00"),dt("2012-12-12T12:12:13-00:00")).apply(dt));
    assertFalse(atBetweenOrBefore(dt("2012-12-12T12:12:10-00:00"),dt("2012-12-12T12:12:12-00:00")).apply(dt));
    
    assertTrue(equivalent(dt, "2012-12-12T12:12:12-00:00"));
    assertTrue(equivalent(toTimeZone(dt,"EST"),"2012-12-12T12:12:12-00:00"));
  }
  
}
