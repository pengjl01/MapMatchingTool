package algorithm.mapmatching.simpledistance;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.MatcherIMPL;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午8:45:33
 * 简单距离匹配
 */
public class SimpleDistance extends MatcherIMPL {

	public SimpleDistance(SpatialIndex index) {
		super(index);
	}

	@Override
	public void matchFeature(int i) {
		String roadID = null;
		PointFeature temp = origin.get(i);
		Point p = temp.getPoint();
		Coordinate GPSCoordinate = p.getCoordinate();
		List<RoadSegment> lines = getPossibleRoads(GPSCoordinate);
		double minDist = Double.POSITIVE_INFINITY;
		Coordinate minDistPoint = null;
		for (RoadSegment road : lines) {
			LocationIndexedLine line = new LocationIndexedLine(road.getGeom());
			LinearLocation here = line.project(GPSCoordinate);
			Coordinate point = line.extractPoint(here);
			double dist = point.distance(GPSCoordinate);
			if (dist < minDist) {
				minDist = dist;
				minDistPoint = point;
				roadID = road.getID();
			}
		}
		if (minDistPoint == null) {
			minDistPoint = GPSCoordinate;
		}
		saveData(i, roadID, minDistPoint);
	}
}
