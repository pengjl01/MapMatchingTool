package algorithm.mapmatching.fwmm;

import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.hmm.HMM;
import algorithm.mapmatching.hmm.HMMNode;
import algorithm.mapmatching.hmm.TPData;
import algorithm.pathfinder.astar.Astar;
import constants.Constants;
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

	protected static double MAX_RADIUS = 50;
	public static Double n1 = 1.879714;
	public static Double n2 = 1.168112;
	public static Double n3 = 3.474735;

	@Override
	protected Map<String, Object> getParamsMap(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate, PointFeature pointFeature, LocationIndexedLine line) {
		Map<String, Object> paramsMap = super.getParamsMap(lineFeature, graph, pCoordinate, closestCoordinate,
				pointFeature, line);
		Double dp = calcDirectionProb(line, closestCoordinate, lineFeature, pointFeature);
		paramsMap.put("dp", dp);
		return paramsMap;
	}

	@Override
	protected TPData getBestTP(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate) {
		TPData ans = new TPData();
		if (preState.size() == 0) {
			ans.tp = 1.0;
		} else {
			// 候选匹配点最近的两个节点
			Coordinate[] nowNodes = lineFeature.getClosestNodes(closestCoordinate);
			if (nowNodes != null) {
//			把当前点添加到图
				if (!graph.cutAndAdd(nowNodes[0], nowNodes[1], closestCoordinate)) {
					return ans;
				}
			}
			for (HMMNode h : preState) {
				if (h.matchedCoor.distance(closestCoordinate) <= Constants.NOT_MOVED_DISTANCE) {
					ans.tp = getTransitionProbility(0, 0) * h.prob;
					ans.parentNode = h;
					break;
				}
				double temptp = getTransitionProbility(graph, closestCoordinate, h);
				if (debug) {
					String preosmid = h.road.getID();
					System.out.println("    road id:" + preosmid + " tp:" + temptp);
				}
				if (temptp > ans.tp) {
					ans.tp = temptp;
					ans.parentNode = h;
				}
			}
			if (nowNodes != null) {
				graph.repareCut(nowNodes[0], nowNodes[1], closestCoordinate);
			}
		}

		return ans;
	}

	/*
	 * tp的计算
	 */
	@Override
	protected double getTransitionProbility(Graph graph, Coordinate closestCoordinate, HMMNode h) {
		if (closestCoordinate.equals(h.matchedCoor))
			return getTransitionProbility(0, 0) * h.prob;
		if (h.nearestNode != null) {
			if (!graph.cutAndAdd(h.nearestNode[0], h.nearestNode[1], h.matchedCoor)) {
				return 0.0;
			}
		}
		Astar astar = new Astar(graph, h.matchedCoor, closestCoordinate);
		if (h.nearestNode != null) {
			graph.repareCut(h.nearestNode[0], h.nearestNode[1], h.matchedCoor);
		}
		return getTransitionProbility(Math.abs(astar.routeDistance()),
				manhattanDistance(h.prepCoordinate, closestCoordinate));
	}

	/*
	 * 求转移概率tp基础版
	 */
	@Override
	protected double getTransitionProbility(double roadDistance, double distance) {
		if (debug) {
			System.out.println("  roadDistance:" + roadDistance + " distance:" + distance);
		}
		double dt = Math.abs(Math.abs(distance) - Math.abs(roadDistance));
		return dt > LowProbabilityRoutes ? 0 : (Math.pow(Math.E, -(dt / BETA)));
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

	@Override
	protected double getEmissionProbility(double distance) {
		if (distance < MAX_RADIUS) {
			return gd.xMoreThanProb(distance);
		} else {
			return 0.0;
		}
	}
//	@Override
//	protected double getEmissionProbility(double distance) {
//		if (distance < MAX_RADIUS) {
//			double num = 1.0 - (distance / MAX_RADIUS);
//			return Math.pow(num, 2);
//		} else {
//			return 0.0;
//		}
//	}

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

	double manhattanDistance(Coordinate a, Coordinate b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
}