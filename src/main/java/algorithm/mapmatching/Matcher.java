package algorithm.mapmatching;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午9:03:05
 * 地图匹配接口
 */
public interface Matcher {
	public SimpleFeatureCollection match(SimpleFeatureCollection points);

	public SimpleFeatureCollection match(SimpleFeatureCollection points, int start, int end);

	public void saveData(int i, String matchedosmid, Coordinate matchedcCoordinate);

	public void saveData(int i, String matchedosmid, Point matchedPoint);
}
