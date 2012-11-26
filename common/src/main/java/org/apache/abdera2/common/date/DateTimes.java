/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.common.date;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.common.selector.Selectors;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Interval;

import static org.apache.abdera2.common.misc.Comparisons.*;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public final class DateTimes {

  private DateTimes() {}
  
  /**
   * Simple Function that creates a DateTime from an object..
   * the input can be a string, a long, a java.util.Date, 
   * a java.util.Calendar or another DateTime. This is 
   * mainly a convenience wrapper for the default constructor
   */
  public static final Function<Object,DateTime> parser = 
    new Function<Object,DateTime>() {
      public DateTime apply(Object input) {
        return new DateTime(input);
      }
  };
  
  /**
   * Simple Function that formats a DateTime object to 
   * ISO8601-compliant string. This is mainly just a 
   * convenience wrapper.
   */
  public static final Function<DateTime,String> formatter = 
    new Function<DateTime,String>() {
      public String apply(DateTime input) {
        return format(input);
      }
  };
  
  private static final DateTimeFormatter DTF =
    ISODateTimeFormat.dateTime();
  
  /**
   * Formats the current date/time to string using the default timezone
   */
  public static String formatNow() {
    return DateTime.now().toString(DTF);
  }
  
  /**
   * Formats the given date/time to string
   */
  public static String format(String dateTime) {
    return DTF.print(new DateTime(dateTime));
  }
  
  /**
   * Formats the given date/time to string
   */
  public static String format(DateTime dateTime) {
    return DTF.print(dateTime);
  }
  
  /**
   * Formats the given date/time to string
   */
  public static String format(Date date) {
    return DTF.print(new DateTime(date));
  }
  
  /**
   * Formats the given date/time to string
   */
  public static String format(Calendar cal) {
    return DTF.print(new DateTime(cal));
  }
  
  /**
   * Formats the given date/time to string
   */
  public static String format(long ms) {
    return DTF.print(ms);
  }
  
  public static Date parse(String t) {
    return DateTime.parse(t).toDate();
  }
  
  /**
   * Converts the given DateTime to the UTC TimeZone
   */
  public static DateTime toUTC(DateTime dt) {
    return dt.toDateTime(DateTimeZone.UTC);
  }
  
  /**
   * Converts the given DateTime to the given TimeZone
   */
  public static DateTime toTimeZone(DateTime dt, TimeZone tz) {
    return dt.toDateTime(DateTimeZone.forTimeZone(tz));
  }
  
  /**
   * Converts the given DateTime to the given TimeZone
   */
  public static DateTime toTimeZone(DateTime dt, String id) {
    return dt.toDateTime(DateTimeZone.forID(id));
  }
  
  /**
   * Use the DateTimeBuilder to generate a DateTime object
   */
  public static DateTimeBuilder makeDateTime() {
    return new DateTimeBuilder();
  }
  
  public static class DateTimeBuilder implements Supplier<DateTime> {
    private int year, month, day, hour, minute, second, millis;
    private DateTimeZone dtz;
    public DateTimeBuilder() {}
    private DateTime _now() {
      return dtz == null ?
        DateTimes.now() :
        DateTimes.now(dtz);
    }
    public DateTimeBuilder thisYear() {
      this.year = _now().year().get();
      return this;
    }
    public DateTimeBuilder thisMonth() {
      this.month = _now().monthOfYear().get();
      return this;
    }
    public DateTimeBuilder thisDay() {
      this.day = _now().dayOfMonth().get();
      return this;
    }
    public DateTimeBuilder thisHour() {
      this.hour = _now().hourOfDay().get();
      return this;
    }
    public DateTimeBuilder thisMinute() {
      this.minute = _now().minuteOfHour().get();
      return this;
    }
    public DateTimeBuilder thisSecond() {
      this.second = _now().secondOfMinute().get();
      return this;
    }
    public DateTimeBuilder thisMillisecond() {
      this.millis = _now().millisOfSecond().get();
      return this;
    }
    public DateTimeBuilder thisTimeZone() {
      this.dtz = _now().getZone();
      return this;
    }
    public DateTimeBuilder thisDate() {
      DateTime now = _now();
      this.year = now.year().get();
      this.month = now.monthOfYear().get();
      this.day = now.dayOfMonth().get();
      return this;
    }
    public DateTimeBuilder thisDatePlusYears(int years) {
      thisDate();
      this.year += years;
      return this;
    }
    public DateTimeBuilder thisDatePlusMonths(int months) {
      thisDate();
      this.year += months / 12;
      this.month += months % 12;
      return this;
    }
    public DateTimeBuilder thisDatePlusDays(int days) {
      thisDate();
      this.year += days / 365;
      int rem = days % 365;
      this.month += rem / 31;
      this.day += rem % 31;
      return this;
    }
    public DateTimeBuilder thisTime() {
      DateTime now = _now();
      this.hour = now.hourOfDay().get();
      this.minute = now.minuteOfHour().get();
      this.second = now.secondOfMinute().get();
      this.millis = now.millisOfSecond().get();
      return this;
    }
    public DateTimeBuilder nowUtc() {
      timezoneUTC();
      DateTime now = _now();
      this.year = now.year().get();
      this.month = now.monthOfYear().get();
      this.day = now.dayOfMonth().get();
      this.hour = now.hourOfDay().get();
      this.minute = now.minuteOfHour().get();
      this.second = now.secondOfMinute().get();
      this.millis = now.millisOfSecond().get();
      return this;
    }
    public DateTimeBuilder now() {
      DateTime now = _now();
      this.year = now.year().get();
      this.month = now.monthOfYear().get();
      this.day = now.dayOfMonth().get();
      this.hour = now.hourOfDay().get();
      this.minute = now.minuteOfHour().get();
      this.second = now.secondOfMinute().get();
      this.millis = now.millisOfSecond().get();
      return this;
    }
    public DateTimeBuilder year(int year) {
      this.year = year;
      return this;
    }
    public DateTimeBuilder month(int month) {
      this.month = month;
      return this;
    }
    public DateTimeBuilder day(int day) {
      this.day = day;
      return this;
    }
    public DateTimeBuilder hour(int hour) {
      this.hour = hour;
      return this;
    }
    public DateTimeBuilder minute(int minute) {
      this.minute = minute;
      return this;
    }
    public DateTimeBuilder second(int second) {
      this.second = second;
      return this;
    }
    public DateTimeBuilder millisecond(int ms) {
      this.millis = ms;
      return this;
    }
    public DateTimeBuilder timezone(TimeZone tz) {
      this.dtz = DateTimeZone.forTimeZone(tz);
      return this;
    }
    public DateTimeBuilder timezoneUTC() {
      this.dtz = DateTimeZone.UTC;
      return this;
    }
    public DateTimeBuilder timezone(String id) {
      this.dtz = DateTimeZone.forID(id);
      return this;
    }
    public DateTimeBuilder timezone(int offsetHours) {
      this.dtz = DateTimeZone.forOffsetHours(offsetHours);
      return this;
    }
    public DateTimeBuilder timezone(int offsetHours, int offsetMinutes) {
      this.dtz = DateTimeZone.forOffsetHoursMinutes(offsetHours, offsetMinutes);
      return this;
    }
    public DateTime get() {
      return new DateTime(
        year,
        Math.min(12,Math.max(1,month)),
        Math.min(31,Math.max(1,day)),
        hour,
        minute,
        second,
        millis,
        dtz);
    }
  }
  
  /**
   * Convenience Utility for Comparing DateTime instances
   */
  public static abstract class DateTimeComparator<X> 
    implements Comparator<X>, Serializable {
    private static final long serialVersionUID = -3081540045542491405L;

    public int innerCompare(DateTime d1, DateTime d2) {
      if (onlySecondIsNull(d1,d2)) return 1;
      if (onlyFirstIsNull(d1,d2)) return -1;
      if (bothAreNull(d1,d2)) return -1;
      int ret = d1.compareTo(d2);
      return ret == 0 ? -1 : ret;
    }
  }

  public static DateTime utc(String dt) {
    return dt(dt, DateTimeZone.UTC);
  }
  
  public static DateTime dt(String dt) {
    return new DateTime(dt);
  }
  
  public static DateTime dt(String dt, DateTimeZone dtz) {
    return new DateTime(dt,dtz);
  }
  
  public static DateTime dt(Date date) {
    return new DateTime(date);
  }
  
  public static DateTime dt(Calendar cal) {
    return new DateTime(cal);
  }
  
  public static DateTime dt(long millis) {
    return new DateTime(millis);
  }
  
  public static DateTime now() {
    return DateTime.now();
  }
  
  public static DateTime now(String tz) {
    return DateTime.now(DateTimeZone.forID(tz));
  }
  
  public static DateTime now(DateTimeZone dtz) {
    return DateTime.now(dtz);
  }
  
  public static DateTime now(TimeZone tz) {
    return DateTime.now(DateTimeZone.forTimeZone(tz));
  }
  
  public static DateTime utcNow() {
    return DateTime.now(DateTimeZone.UTC);
  }
  
  public static Selector<DateTime> selectorForRange(Range<DateTime> range) {
    return Selectors.forPredicate(range);
  }
  
  public static Range<DateTime> all() {
    return Ranges.<DateTime>all();
  }
  
  public static Range<DateTime> atOrAfterNow() {
    return atOrAfter(DateTime.now());
  }
  
  public static Range<DateTime> atOrAfter(DateTime dateTime) {
    return Ranges.<DateTime>atLeast(dateTime);
  }
  
  public static Range<DateTime> atOrBefore(DateTime dateTime) {
    return Ranges.<DateTime>atMost(dateTime);
  }
  
  public static Range<DateTime> atOrBetween(DateTime low, DateTime high) {
    return Ranges.<DateTime>closed(low,high);
  }
  
  public static Range<DateTime> atOrBetween(DateTime low, Duration duration) {
    return atOrBetween(low,low.plus(duration));
  }
  
  public static Range<DateTime> atOrBetween(Duration duration, DateTime high) {
    return atOrBetween(high.minus(duration),high);
  }
  
  public static Range<DateTime> atOrBetween(Interval interval) {
    return atOrBetween(interval.getStart(),interval.getEnd());
  }
  
  public static Range<DateTime> atBetweenOrBefore(DateTime low, DateTime high) {
    return Ranges.<DateTime>closedOpen(low,high);
  }
  
  public static Range<DateTime> atBetweenOrBefore(DateTime low, Duration duration) {
    return atBetweenOrBefore(low,low.plus(duration));
  }
  
  public static Range<DateTime> atBetweenOrBefore(Duration duration, DateTime high) {
    return atBetweenOrBefore(high.minus(duration),high);
  }
  
  public static Range<DateTime> atBetweenOrBefore(Interval interval) {
    return atBetweenOrBefore(interval.getStart(),interval.getEnd());
  }
  
  public static Range<DateTime> afterNow() {
    return after(DateTime.now());
  }
  
  public static Range<DateTime> after(DateTime dateTime) {
    return Ranges.<DateTime>greaterThan(dateTime);
  }
  
  public static Range<DateTime> beforeNow() {
    return before(DateTime.now());
  }
  
  public static Range<DateTime> before(DateTime dateTime) {
    return Ranges.<DateTime>lessThan(dateTime);
  }
  
  public static Range<DateTime> between(DateTime low, DateTime high) {
    return Ranges.<DateTime>open(low,high);
  }
  
  public static Range<DateTime> between(DateTime low, Duration duration) {
    return between(low,low.plus(duration));
  }
  
  public static Range<DateTime> between(Duration duration, DateTime high) {
    return between(high.minus(duration), high);
  }
  
  public static Range<DateTime> between(Interval interval) {
    return between(interval.getStart(),interval.getEnd());
  }
  
  public static Range<DateTime> afterBetweenOrAt(DateTime low, DateTime high) {
    return Ranges.<DateTime>openClosed(low,high);
  }
  
  public static Range<DateTime> afterBetweenOrAt(DateTime low, Duration duration) {
    return afterBetweenOrAt(low,low.plus(duration));
  }
  
  public static Range<DateTime> afterBetweenOrAt(Duration duration, DateTime high) {
    return afterBetweenOrAt(high.minus(duration), high);
  }
  
  public static Range<DateTime> exactlyNow() {
    return exactly(DateTime.now());
  }
  
  public static Range<DateTime> exactly(DateTime dateTime) {
    return Ranges.<DateTime>singleton(dateTime);
  }
  
  public static Range<DateTime> exactlyBefore(DateTime date, Duration duration) {
    return exactly(date.minus(duration));
  }
  
  public static Range<DateTime> exactlyAfter(DateTime date, Duration duration) {
    return exactly(date.plus(duration));
  }
  
  public static Range<DateTime> exactlyBeforeNow(Duration duration) {
    return exactlyBefore(DateTime.now(),duration);
  }
  
  public static Range<DateTime> exactlyAfterNow(Duration duration) {
    return exactlyAfter(DateTime.now(),duration);
  }
  
  public static final boolean equivalent(DateTime d1, DateTime d2) {
    return Equivalence.equivalent(d1, d2);
  }
  
  public static final boolean equivalent(DateTime d1, Date date) {
    return equivalent(d1, new DateTime(date));
  }
  
  public static final boolean equivalent(DateTime d1, Calendar cal) {
    return equivalent(d1, new DateTime(cal));
  }
  
  public static final boolean equivalent(DateTime d1, long time) {
    return equivalent(d1, new DateTime(time));
  }
  
  public static final boolean equivalent(DateTime d1, String obj) {
    return equivalent(d1, new DateTime(obj));
  }
  
  public static final boolean equivalent(Date d1, Date date) {
    return equivalent(new DateTime(d1), new DateTime(date));
  }
  
  public static final boolean equivalent(Date d1, Calendar cal) {
    return equivalent(new DateTime(d1), new DateTime(cal));
  }
  
  public static final boolean equivalent(Date d1, long time) {
    return equivalent(new DateTime(d1), new DateTime(time));
  }
  
  public static final boolean equivalent(Date d1, String obj) {
    return equivalent(new DateTime(d1), new DateTime(obj));
  }
  
  public static final boolean equivalent(Calendar c1, Calendar c2) {
    return equivalent(new DateTime(c1),new DateTime(c2));
  }
  
  public static final boolean equivalent(Calendar c1, long time) {
    return equivalent(new DateTime(c1), new DateTime(time));
  }
  
  public static final boolean equivalent(Calendar c1, String c2) {
    return equivalent(new DateTime(c1), new DateTime(c2));
  }
  
  public static final boolean equivalent(long c1, long c2) {
    return equivalent(new DateTime(c1), new DateTime(c2));
  }
  
  public static final boolean equivalent(long c1, String c2) {
    return equivalent(new DateTime(c1), new DateTime(c2));
  }
  
  public static final boolean equivalent(String c1, String c2) {
    return equivalent(new DateTime(c1), new DateTime(c2));
  }
  
  public static Predicate<DateTime> equivalentTo(Date date) {
    return equivalentTo(new DateTime(date));
  }
  
  public static Predicate<DateTime> equivalentTo(Calendar cal) {
    return equivalentTo(new DateTime(cal));
  }
  
  public static Predicate<DateTime> equivalentTo(long time) {
    return equivalentTo(new DateTime(time));
  }
  
  public static Predicate<DateTime> equivalentTo(String dt) {
    return equivalentTo(new DateTime(dt));
  }
  
  public static Predicate<DateTime> equivalentTo(DateTime dateTime) {
    return Equivalence.equivalentTo(dateTime);
  }
  
  public static final Equivalence<DateTime> Equivalence = equivalence();
  
  private static Equivalence<DateTime> equivalence() {
    return new Equivalence<DateTime>() {
      protected boolean doEquivalent(DateTime a, DateTime b) {
        return a.compareTo(b) == 0;
      }
      protected int doHash(DateTime t) {
        return toUTC(t).hashCode();
      }
    };
  }
  
  public static Selector<DateTime> selectorForAll() {
    return selectorForRange(all());
  }
  
  public static Selector<DateTime> selectorForAtOrAfterNow() {
    return selectorForRange(atOrAfterNow());
  }
  
  public static Selector<DateTime> selectorForAtOrAfter(DateTime dateTime) {
    return selectorForRange(atOrAfter(dateTime));
  }
  
  public static Selector<DateTime> selectorForAtOrBefore(DateTime dateTime) {
    return selectorForRange(atOrBefore(dateTime));
  }
  
  public static Selector<DateTime> selectorForAtOrBetween(DateTime low, DateTime high) {
    return selectorForRange(atOrBetween(low,high));
  }
  
  public static Selector<DateTime> selectorForAtOrBetween(DateTime low, Duration duration) {
    return selectorForRange(atOrBetween(low,low.plus(duration)));
  }
  
  public static Selector<DateTime> selectorForAtOrBetween(Duration duration, DateTime high) {
    return selectorForRange(atOrBetween(high.minus(duration),high));
  }
  
  public static Selector<DateTime> selectorForAtOrBetween(Interval interval) {
    return selectorForRange(atOrBetween(interval.getStart(),interval.getEnd()));
  }
  
  public static Selector<DateTime> selectorForAtBetweenOrBefore(DateTime low, DateTime high) {
    return selectorForRange(atBetweenOrBefore(low,high));
  }
  
  public static Selector<DateTime> selectorForAtBetweenOrBefore(DateTime low, Duration duration) {
    return selectorForRange(atBetweenOrBefore(low,duration));
  }
  
  public static Selector<DateTime> selectorForAtBetweenOrBefore(Duration duration, DateTime high) {
    return selectorForRange(atBetweenOrBefore(duration,high));
  }
  
  public static Selector<DateTime> selectorForAtBetweenOrBefore(Interval interval) {
    return selectorForRange(atBetweenOrBefore(interval));
  }
  
  public static Selector<DateTime> selectorForAfterNow() {
    return selectorForRange(afterNow());
  }
  
  public static Selector<DateTime> selectorForAfter(DateTime dateTime) {
    return selectorForRange(after(dateTime));
  }
  
  public static Selector<DateTime> selectorForBeforeNow() {
    return selectorForRange(beforeNow());
  }
  
  public static Selector<DateTime> selectorForBefore(DateTime dateTime) {
    return selectorForRange(before(dateTime));
  }
  
  public static Selector<DateTime> selectorForBetween(DateTime low, DateTime high) {
    return selectorForRange(between(low,high));
  }
  
  public static Selector<DateTime> selectorForBetween(DateTime low, Duration duration) {
    return selectorForRange(between(low,duration));
  }
  
  public static Selector<DateTime> selectorForBetween(Duration duration, DateTime high) {
    return selectorForRange(between(duration,high));
  }
  
  public static Selector<DateTime> selectorForBetween(Interval interval) {
    return selectorForRange(between(interval));
  }
  
  public static Selector<DateTime> selectorForAfterBetweenOrAt(DateTime low, DateTime high) {
    return selectorForRange(afterBetweenOrAt(low,high));
  }
  
  public static Selector<DateTime> selectorForAfterBetweenOrAt(DateTime low, Duration duration) {
    return selectorForRange(afterBetweenOrAt(low,duration));
  }
  
  public static Selector<DateTime> selectorForAfterBetweenOrAt(Duration duration, DateTime high) {
    return selectorForRange(afterBetweenOrAt(duration,high));
  }
  
  public static Selector<DateTime> selectorForExactlyNow() {
    return selectorForRange(exactlyNow());
  }
  
  public static Selector<DateTime> selectorForExactly(DateTime dateTime) {
    return selectorForRange(exactly(dateTime));
  }
  
  public static Selector<DateTime> selectorForExactlyBefore(DateTime date, Duration duration) {
    return selectorForRange(exactlyBefore(date,duration));
  }
  
  public static Selector<DateTime> selectorForExactlyAfter(DateTime date, Duration duration) {
    return selectorForRange(exactlyAfter(date,duration));
  }
  
  public static Selector<DateTime> selectorForExactlyBeforeNow(Duration duration) {
    return selectorForRange(exactlyBeforeNow(duration));
  }
  
  public static Selector<DateTime> selectorForExactlyAfterNow(Duration duration) {
    return selectorForRange(exactlyAfterNow(duration));
  }
}
