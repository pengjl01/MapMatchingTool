package entity.graph;

import static org.junit.Assert.assertEquals;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;

/*
 * @author pjl
 * @version 创建时间：2019年6月26日 下午1:53:16
 * 类说明
 */
public class GraphToolsTest extends GraphTools {
	@Test
	public void baseTestThreePointCollinearity1() throws Exception {
		Coordinate a = new Coordinate(1, 1);
		Coordinate b = new Coordinate(1, 9);
		Coordinate c = new Coordinate(3, 5);
		assertEquals("(1,1)(1,9)(3,5)", false, GraphTools.threePointCollinearity(a, b, c));
	}

	@Test
	public void baseTestThreePointCollinearity2() throws Exception {
		Coordinate a = new Coordinate(83.45, 1519.221);
		Coordinate b = new Coordinate(821.33, 14564.9394);
		Coordinate c = new Coordinate(647.94, 11499.4042);
		assertEquals("(83.45, 1519.221)(821.33, 14564.9394)(647.94, 11499.4042)", true,
				GraphTools.threePointCollinearity(a, b, c));
	}

	public static void main(String[] args) {
		Coordinate a = new Coordinate(9936340.638872877, 4444949.318561003);
		Coordinate b = new Coordinate(9936342.594542818, 4444949.552331933);
		Coordinate c = new Coordinate(9936341.548867108, 4444949.427337128);
		double s = (a.x * b.y - b.x * a.y) + (b.x * c.y - c.x * b.y) + (c.x * a.y - a.x * c.y);
		double l = a.distance(b);
		System.out.println(s + " " + l);
		System.out.println(Math.abs(s / l / l));
		WKTReader reader = new WKTReader(JTSFactoryFinder.getGeometryFactory());
		try {
			MultiLineString geom1 = (MultiLineString) reader.read(
					"MULTILINESTRING ((9936340.638872877 4444949.318561003, 9936342.594542818 4444949.552331933))");
			LocationIndexedLine lil = new LocationIndexedLine(geom1);
			LinearLocation ll = lil.project(c);
			Coordinate d = lil.extractPoint(ll);
			System.out.println(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		begintime = System.nanoTime();
//		for (int i = 0; i < max; ++i) {
//			threePointCollinearity0(a, b, c);
//		}
//		endtime = System.nanoTime();
//		System.out.println("time0:" + (endtime - begintime));
//		begintime = System.nanoTime();
//		for (int i = 0; i < max; ++i) {
//			threePointCollinearity1(a, b, c);
//		}
//		endtime = System.nanoTime();
//		System.out.println("time1:" + (endtime - begintime));

	}
}
