package data.simplefeature.roadsegment;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;
import org.opengis.feature.simple.SimpleFeature;

/*
 * @author pjl
 * @version 创建时间：2019年6月26日 下午12:32:35
 * OSM地图数据格式的roadsegment
 */
public class OSMRoadSegment implements RoadSegment {
	SimpleFeature road;

	public OSMRoadSegment(SimpleFeature road) {
		this.road = road;
	}

	@Override
	public String getID() {
		return (String) road.getProperty("osm_id").getValue();
	}

	@Override
	public char getOneway() {
		return ((String) road.getProperty("oneway").getValue()).charAt(0);
	}

	@Override
	public MultiLineString getGeom() {
		return (MultiLineString) road.getProperty("the_geom").getValue();
	}

	@Override
	public Coordinate[] getClosestNodes(Coordinate c) {
		LocationIndexedLine line = new LocationIndexedLine(this.getGeom());
		LinearLocation here = line.project(c);
		if (here.getSegmentFraction() == 0) {
			return null;
		} else {
			Coordinate[] ans = new Coordinate[2];
			ans[0] = line.extractPoint(new LinearLocation(here.getComponentIndex(), here.getSegmentIndex(), 0));
			ans[1] = line.extractPoint(new LinearLocation(here.getComponentIndex(), here.getSegmentIndex() + 1, 0));
			return ans;
		}
	}

	@Override
	public Double getSpeed() {
		return Double.valueOf((Integer) road.getProperty("maxspeed").getValue());
	}
}
