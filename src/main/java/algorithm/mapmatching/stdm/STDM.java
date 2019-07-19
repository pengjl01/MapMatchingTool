package algorithm.mapmatching.stdm;

import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.fwmm.Tools;
import algorithm.mapmatching.hmm.HMM;
import algorithm.mapmatching.hmm.TPData;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;
import entity.graph.Graph;

/*
 * @author pjl
 * @version 创建时间：2019年7月17日 下午7:42:57
 * stdm
 * https://www.sciencedirect.com/science/article/pii/S0020025517311544?via%3Dihub#sec0013
 */
public class STDM extends HMM {

	public STDM(SpatialIndex index) {
		super(index);
	}

	public STDM(SpatialIndex index, boolean debug) {
		super(index, debug);
	}

	@Override
	protected Map<String, Object> getParamsMap(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate, PointFeature pointFeature, LocationIndexedLine line) {
		Map<String, Object> paramsMap = super.getParamsMap(lineFeature, graph, pCoordinate, closestCoordinate,
				pointFeature, line);
		Double dp = calcDirectionProb(line, closestCoordinate, lineFeature, pointFeature);
		paramsMap.put("dp", dp);
		Double sp = calcSpeedProb(lineFeature, pointFeature);
		paramsMap.put("sp", sp);
		return paramsMap;
	}

	private Double calcSpeedProb(RoadSegment lineFeature, PointFeature pointFeature) {
		Double roadSpeed = lineFeature.getSpeed();
		Double carSpeed = pointFeature.getSpeed();
		return roadSpeed * carSpeed / (Math.pow(roadSpeed, 2) + Math.pow(carSpeed, 2));
	}

	// 由参数map计算最终权值
	@Override
	protected double calcProb(Map<String, Object> paramsMap) {
		double ep = (double) paramsMap.get("ep");
		TPData tpData = (TPData) paramsMap.get("tpData");
		Double dp = (Double) paramsMap.get("dp");
		Double sp = (Double) paramsMap.get("sp");
		Double prob = ep * tpData.tp * dp * sp;
		return prob;
	}

	@Override
	protected double getTransitionProbility(double roadDistance, double distance) {
		return distance / roadDistance;
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
		return 1.0;
	}

	Double calcDirectionProb(Double d) {
		if (d == null)
			return 0.0;
		return gd.probabilityDensity(d);
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
