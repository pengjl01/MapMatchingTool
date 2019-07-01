package algorithm.mapmatching;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import constants.Constants;
import data.simplefeature.pointfeature.BJTaxiData;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.pointfeature.PointFeatureBuilder;
import data.simplefeature.roadsegment.OSMRoadSegment;
import data.simplefeature.roadsegment.RoadSegment;

/*
 * @author pjl
 * @version 创建时间：2019年4月10日 下午5:21:40
 * 地图匹配抽象类
 */
public abstract class MatcherIMPL implements Matcher {
	protected SpatialIndex index;
	protected GeometryFactory geometryFactory;
	protected List<PointFeature> origin;
	protected PointFeatureBuilder pfb;
	protected Point[] matchedPoints;
	protected String[] matchedIds;
	/*
	 * for debug only
	 */
	protected boolean debug = false;
	int matchnum;

	public MatcherIMPL(SpatialIndex index) {
		this.index = index;
		geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	}

	public MatcherIMPL(SpatialIndex index, boolean debug) {
		this.debug = debug;
		this.index = index;
		geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	}

	/*
	 * 执行匹配流程
	 */
	@Override
	public final SimpleFeatureCollection match(SimpleFeatureCollection points) {
		return match(points, 0, origin.size());
	};

	@Override
	public final SimpleFeatureCollection match(SimpleFeatureCollection points, int start, int end) {
		if (end == -1)
			end = origin.size();
		pfb = new PointFeatureBuilder(points);
		initMatch(points);
		match(start, end);
		return genarateSFC(pfb.getFeatureType(), start, end);
	};

	protected void match(int start, int end) {
		for (int i = start; i < end; ++i)
			matchFeature(i);
	}

	/*
	 * 抽象方法，对某一特定点进行匹配
	 */
	protected abstract void matchFeature(int i);

	protected void initMatch(SimpleFeatureCollection points) {
		origin = sfc2PointFeatureList(points);
		matchedIds = new String[origin.size()];
		matchedPoints = new Point[origin.size()];
	}

	protected List<PointFeature> sfc2PointFeatureList(SimpleFeatureCollection points) {
		List<PointFeature> simpleFeatureList = new ArrayList<PointFeature>();
		SimpleFeatureIterator i = points.features();
		try {
			while (i.hasNext()) {
				simpleFeatureList.add(new BJTaxiData(i.next()));
			}
		} finally {
			i.close();
		}
		return simpleFeatureList;
	}

	@Override
	public void saveData(int i, String matchedosmid, Coordinate matchedcCoordinate) {
		saveData(i, matchedosmid, geometryFactory.createPoint(matchedcCoordinate));
	}

	@Override
	public void saveData(int i, String matchedosmid, Point matchedPoint) {
		matchedIds[i] = matchedosmid;
		matchedPoints[i] = matchedPoint;
	}

	protected SimpleFeatureCollection genarateSFC(SimpleFeatureType sft, int start, int end) {
		List<SimpleFeature> ans = new ArrayList<SimpleFeature>();
		for (int i = start; i < end; ++i) {
			ans.add(pfb.buildFeature(origin.get(i), matchedIds[i], matchedPoints[i]));
		}
		return new ListFeatureCollection(sft, ans);
	}

//	public static List<Point> sfc2PointList(SimpleFeatureCollection points) {
//		List<Point> pointsList = new ArrayList<Point>();
//		SimpleFeatureIterator i = points.features();
//		try {
//			while (i.hasNext()) {
//				Point temp = (Point) i.next().getProperty("the_geom").getValue();
//				pointsList.add(temp);
//			}
//		} finally {
//			i.close();
//		}
////		for (int i = 0; i < pointsList.size() - 1; ++i) {
////			double dist = pointsList.get(i).distance(pointsList.get(i + 1));
////			System.out.println("point1:" + pointsList.get(i) + " point2:" + pointsList.get(i + 1));
////			System.out.println("dist:" + dist);
////		}
//		return pointsList;
//	}

//	public static SimpleFeatureCollection matchDiff(SimpleFeatureCollection points,
//			SimpleFeatureCollection mathcedPoints) {
//		SimpleFeatureTypeBuilder modificationType = new SimpleFeatureTypeBuilder();
//		modificationType.setName("Differents");
//		modificationType.setCRS(DefaultGeographicCRS.WGS84);
//		modificationType.add("the_geom", LineString.class);
//		SimpleFeatureBuilder modificationBuilder = new SimpleFeatureBuilder(modificationType.buildFeatureType());
//		DefaultFeatureCollection modificationCollection = new DefaultFeatureCollection();
//		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//		List<Point> pointsList = sfc2PointList(points);
//		List<Point> matchedpointsList = sfc2PointList(points);
//		if (pointsList.size() != matchedpointsList.size())
//			System.out.println("Matcher.matchDiff : Two SimpleFeatureCollection has different size.");
//		for (int i = 0; i < pointsList.size(); ++i) {
//			Coordinate[] lineCorrectionCoordinates = new Coordinate[] { pointsList.get(i).getCoordinate(),
//					matchedpointsList.get(i).getCoordinate() };
//			LineString correctionLine = geometryFactory.createLineString(lineCorrectionCoordinates);
//			modificationBuilder.add(correctionLine);
//			modificationCollection.add(modificationBuilder.buildFeature(null));
//		}
//		return modificationCollection;
//	}
	/*
	 * 取出周围的道路
	 */

	protected List<RoadSegment> getPossibleRoads(Coordinate GPSCoordinate, double distance) {
		Envelope search = new Envelope(GPSCoordinate);
		search.expandBy(distance);
		List<RoadSegment> ans = new ArrayList<RoadSegment>();
		List<SimpleFeature> sfans = index.query(search);
		for (SimpleFeature s : sfans) {
			ans.add(new OSMRoadSegment(s));
		}
		return ans;
	}

	protected List<RoadSegment> getPossibleRoads(Coordinate GPSCoordinate) {
		return getPossibleRoads(GPSCoordinate, Constants.MAX_SEARCH_DISTANCE);
	}
}
