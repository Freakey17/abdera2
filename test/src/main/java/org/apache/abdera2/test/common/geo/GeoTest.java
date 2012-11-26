package org.apache.abdera2.test.common.geo;

import org.apache.abdera2.common.geo.Box;
import org.apache.abdera2.common.geo.Coordinate;
import org.apache.abdera2.common.geo.IsoPosition;
import org.apache.abdera2.common.geo.Line;
import org.apache.abdera2.common.geo.Point;
import org.apache.abdera2.common.geo.Polygon;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class GeoTest {

  @Test
  public void isoPositionTest() {
    IsoPosition iso = IsoPosition.parse("+12.1234567+123.1234567+123.1234567");
    assertEquals(12.1234567,iso.getLatitude(),0);
    assertEquals(123.1234567,iso.getLongitude(),0);
    assertEquals(123.1234567,iso.getAltitude(),0);
    assertEquals("+12.123457+123.123457+123.123457/", iso.toString());
  }
  
  @Test
  public void boxTest() {
    Coordinate c1 = new Coordinate(1.0,2.0);
    Coordinate c2 = new Coordinate(3.0,4.0);
    Box box = Box.at(c1,c2);
    assertEquals(c1,box.getLowerCorner());
    assertEquals(c2,box.getUpperCorner());
  }
  
  @Test
  public void coordinateTest() {
    Coordinate c1 = new Coordinate(1.0,2.0);
    assertEquals(1.0,c1.getLatitude(),0);
    assertEquals(2.0,c1.getLongitude(),0);
  }

  @Test
  public void lineTest() {
    Line.with(
      Coordinate.at(1.0,2.0),
      Coordinate.at(3.0,4.0),
      Coordinate.at(5.0,6.0)
    );
  }
  
  
  @Test
  public void pointTest() {
    Coordinate c = new Coordinate(1.0,2.0);
    Point point = Point.at(c);
    assertEquals(1.0,point.getCoordinate().getLatitude(),0);
  }
  
  @Test
  public void polygonTest() {
    Polygon.with(
      Coordinate.at(1.0,2.0),
      Coordinate.at(3.0,4.0),
      Coordinate.at(5.0,6.0)
    );
  }
  
}
