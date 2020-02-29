package data.tools;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/*
 * @author pjl
 * @version 创建时间：2019年4月16日 下午6:41:26
 * 经纬度坐标转换工具类
 * 经纬度转网格
 */
public class GridTrans {
//	地球半径6378137米
	public static final double R = 6378137;
	public static final double BJLATITUDE = 39.90;
	public static final double BJLONGTITUDE = 116.40;
	public static final double LATITUDERBJ = latitudeR(BJLATITUDE);

//  计算纬度圈半径
	public static double latitudeR(double latitude) {
		return R * Math.cos(Math.toRadians(latitude));
	}

	/*
	 * 将经纬度平面化。c：先经后纬
	 */
	public static void trans(Coordinate c) {
		c.setX(LATITUDERBJ * Math.toRadians(c.x));
		c.setY(R * Math.toRadians(c.y));
	}

//	public static Coordinate trans(Coordinate c) {
//		return new Coordinate(R * Math.toRadians(c.x), latitudeR(c.x) * Math.toRadians(c.y));
//	}
	/*
	 * 将经纬度平面化。c：先经后纬
	 */
	public static void trans(Geometry geom) {
		if (geom instanceof LineString || geom instanceof MultiLineString) {
			int parts = geom.getNumGeometries();
			for (int i = 0; i < parts; i++) {
				LineString l = (LineString) geom.getGeometryN(i);
				for (int j = 0, num = l.getNumPoints(); j < num; j++) {
					Coordinate coor = l.getCoordinateN(j);
					trans(coor);
				}
			}
		} else if (geom instanceof Point) {
			Coordinate coor = geom.getCoordinate();
			trans(coor);
		} else {
			System.out.println("CoordinateTrans Error: Unknown geom type");
		}
	}

	/*
	 * 将经纬度平面化。c：先经后纬
	 */
	public static void transBack(Coordinate c) {
		c.setX(Math.toDegrees(c.x / R));
		c.setY(Math.toDegrees(c.y / LATITUDERBJ));
	}

	/*
	 * 将经纬度平面化还原。c：先经后纬
	 */
	public static void transBack(Geometry geom) {
		if (geom instanceof LineString || geom instanceof MultiLineString) {
			int parts = geom.getNumGeometries();
			for (int i = 0; i < parts; i++) {
				LineString l = (LineString) geom.getGeometryN(i);
				for (int j = 0, num = l.getNumPoints(); j < num; j++) {
					Coordinate coor = l.getCoordinateN(j);
					transBack(coor);
				}
			}
		} else if (geom instanceof Point) {
			Coordinate coor = geom.getCoordinate();
			transBack(coor);
		}
	}

	public static void main(String[] args) {
		WKTReader reader = new WKTReader(JTSFactoryFinder.getGeometryFactory());
		try {
			Geometry mls = reader.read(
					"MULTILINESTRING ((-115.9767 40.2659, -115.9767 39.5943, -116.8491 39.5943, -116.8491 40.2659))");
			System.out.println(mls);
			trans(mls);
			System.out.println(mls);
			transBack(mls);
			System.out.println(mls);
			Geometry ls = reader
					.read("LINESTRING (115.9767 40.2659, 115.9767 39.5943, 116.8491 39.5943, 116.8491 40.2659)");
			System.out.println(ls);
			trans(ls);
			System.out.println(ls);
			transBack(ls);
			System.out.println(ls);
			Geometry p = reader.read("POINT (115.9767 40.2659)");
			System.out.println(p);
			trans(p);
			System.out.println(p);
			transBack(p);
			System.out.println(p);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
