package algorithm.mapmatching.fwmm;

import java.util.List;
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
public class WMM extends HMM {
	public static double DEFAULT_DIRECTION_PROB = 0.7;

	public WMM(SpatialIndex index) {
		super(index);
	}

	public WMM(SpatialIndex index, boolean debug) {
		super(index, debug);
	}

	public static Double n1 = 1.0;
	public static Double n2 = 1.0;
	public static Double n3 = 2.0;

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
		Double prob = Math.pow(ep, n1) * Math.pow(tpData.tp, n2) * Math.pow(dp, n3);
		return prob;
	}

	Double calcDirectionProb(LocationIndexedLine line, Coordinate closestCoordinate, RoadSegment lineFeature,
			PointFeature pointFeature) {
		char oneway = lineFeature.getOneway();
		Double direction = pointFeature.getDirection();
		if (direction > 0) {
			List<Double> lineDirection = Tools.getRoadDirection(line, closestCoordinate, oneway);
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

	Double calcDirectionProb(Double d1, List<Double> d2) {
		Double ans = 0.0;
		if (d1 != null && d2 != null && d2.size() != 0) {
			for (Double d : d2) {
				if (d != null) {
					Double temp = calcDirectionProb(Math.abs(d - d1));
					ans = temp > ans ? temp : ans;
				}
			}
		}
		return ans;
	}
}