package algorithm.mapmatching.fwmm;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;

/*
 * @author pjl
 * @version 创建时间：2019年4月2日 下午6:09:28
 * 工具类
 */
public class Tools {
	/*
	 * 给定两个坐标p1 p2，计算射线p1-p2与y轴（正北方向）的夹角
	 */
	public static double calcDirection(Coordinate p1, Coordinate p2) {
		return Math.atan2((p2.x - p1.x), (p2.y - p1.y));
	}

	/*
	 * 返回匹配点c处道路的方向，一个长度为2的double型数组，根据方向和坐标位置进行筛选，如果出现非可能的点，返回的值可能为null 非可能方向：
	 * 1.当匹配位置为顶点时，该顶点为最后一个顶点，则后方向为null 2.当匹配位置为中间或顶点时，方向为单向的道路，则错误的方向返回值为null
	 */
	public static Double[] getRoadDirection(LocationIndexedLine line, Coordinate c, char oneway) {
		LinearLocation here = line.project(c);
		Double[] ans = new Double[2];
		LinearLocation ll1, ll2;
		if (here.getSegmentFraction() == 0) {
			ll1 = new LinearLocation(here.getComponentIndex(), here.getSegmentIndex() - 1, 0);
			ll2 = new LinearLocation(here.getComponentIndex(), here.getSegmentIndex() + 1, 0);
		} else {
			ll1 = new LinearLocation(here.getComponentIndex(), here.getSegmentIndex(), 0);
			ll2 = new LinearLocation(here.getComponentIndex(), here.getSegmentIndex() + 1, 0);
		}
		Coordinate c1 = null, c2 = null;
//		不是T意味着该道路可以正向行驶
		if (oneway != 'T') {
			try {
				c1 = line.extractPoint(ll1);
			} catch (Exception e) {
			}
		}

//		不是F意味着该道路可以逆向行驶
		if (oneway != 'F') {
			try {
				c2 = line.extractPoint(ll2);
			} catch (Exception e) {
			}
		}
		ans[0] = c1 == null ? null : calcDirection(c, c1);
		ans[1] = c2 == null ? null : calcDirection(c, c2);
		return ans;
	}
//	public static List<LocationIndexedLine> getPossibleRoads2(Coordinate GPSCoordinate, SpatialIndex index,
//			double distance) {
//		Envelope search = new Envelope(GPSCoordinate);
//		search.expandBy(distance);
//		return index.query(search);
//	}
//
//	public static List<LocationIndexedLine> getPossibleRoads2(Coordinate GPSCoordinate, SpatialIndex index) {
//		return getPossibleRoads2(GPSCoordinate, index, Constants.MAX_SEARCH_DISTANCE);
//	}

	public static void main(String[] args) {
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		String wktPoint = "POINT(1.5 1.5)";
		String wktLine = "LINESTRING(0 0,3 3,3 5)";
		try {
//			long start = System.currentTimeMillis();

			Geometry geom1 = reader.read(wktPoint);
			System.out.println(geom1);
			Coordinate a = geom1.getCoordinate();
			System.out.println(a);
			Coordinate b = new Coordinate(3, 4);
			System.out.println(b);
			Geometry geom2 = reader.read(wktLine);
			System.out.println(geom2);
			LocationIndexedLine line = new LocationIndexedLine(geom2);
			System.out.println(line);
			LinearLocation herea = line.project(a);
			System.out.println(herea);
			LinearLocation hereb = line.project(b);
			System.out.println(hereb);
			LinearLocation hereb2 = new LinearLocation(hereb.getComponentIndex(), hereb.getSegmentIndex(), 0);
			System.out.println(hereb2);
			System.out.println(line.extractPoint(hereb2));
			LinearLocation hereb3 = new LinearLocation(hereb.getComponentIndex(), hereb.getSegmentIndex(), 1);
			System.out.println(hereb3);
			System.out.println(line.extractPoint(hereb3));
			LinearLocation hereb4 = new LinearLocation(hereb.getComponentIndex(), hereb.getSegmentIndex() + 1, 1);
			System.out.println(hereb4);
			System.out.println(line.extractPoint(hereb4));
//			String inputShp = "D:\\data\\beijing\\China4326.shp", outputShp = "D:\\data\\beijing\\China3857.shp";
//			System.out.println("坐标转换完成，共耗时" + (System.currentTimeMillis() - start) + "ms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
