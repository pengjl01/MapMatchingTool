package algorithm.mapmatching.fwmm;

import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.hmm.HMM;
import algorithm.mapmatching.hmm.TPData;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;
import entity.graph.Graph;

/*
 * @author pjl
 * @version 创建时间：2019年4月4日 下午1:58:28
 * 类说明
 */
public class FWMM extends HMM {
	public static double DEFAULT_DIRECTION_PROB = 0.7;

	public FWMM(SpatialIndex index) {
		super(index);
	}

	public FWMM(SpatialIndex index, boolean debug) {
		super(index, debug);
	}

	@Override
	protected Map<String, Object> getParamsMap(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate, PointFeature pointFeature, LocationIndexedLine line) {
		Map<String, Object> paramsMap = super.getParamsMap(lineFeature, graph, pCoordinate, closestCoordinate,
				pointFeature, line);
		Double dp = calcDirectionProb(line, closestCoordinate, lineFeature, pointFeature);
		paramsMap.put("dp", dp);
		return paramsMap;
	}

//	由参数map计算最终权值
	@Override
	protected double calcProb(Map<String, Object> paramsMap) {
		double ep = (double) paramsMap.get("ep");
		TPData tpData = (TPData) paramsMap.get("tpData");
		Double dp = (Double) paramsMap.get("dp");
		return ep * tpData.tp * dp;
	}

	Double calcDirectionProb(LocationIndexedLine line, Coordinate closestCoordinate, RoadSegment lineFeature,
			PointFeature pointFeature) {
		char oneway = lineFeature.getOneway();
		Double direction = pointFeature.getDirection();
		if (direction > 0) {
			Double[] lineDirection = Tools.getRoadDirection(line, closestCoordinate, oneway);
			Double directionProb = calcDirectionProb(direction, lineDirection);
			if (debug && directionProb > 0) {
				System.out.print("  directionProb:" + directionProb + "  pointDirection:" + direction * 180 / Math.PI
						+ "  lineDirection:");
				for (Double d : lineDirection) {
					if (d != null)
						System.out.print(d * 180 / Math.PI + ";");
				}
				System.out.println();
			}
			return directionProb;
		}
		return DEFAULT_DIRECTION_PROB;
	}

	Double calcDirectionProb(Double d) {
		if (d == null)
			return 0.0;
		Double dp = Math.cos(d);
		return dp < 0 ? 0.0 : dp;
	}

	Double calcDirectionProb(Double d1, Double d2) {
		if (d1 == null || d2 == null)
			return 0.0;
		return calcDirectionProb(d1 - d2);
	}

	Double calcDirectionProb(Double d1, Double[] d2) {
		if (d1 == null || d2 == null || d2.length == 0)
			return 0.0;
		Double delta = Math.PI;
		for (Double d : d2) {
			if (d != null) {
				Double temp = Math.abs(d - d1);
				delta = temp < delta ? temp : delta;
			}
		}
		return calcDirectionProb(delta);
	}
}