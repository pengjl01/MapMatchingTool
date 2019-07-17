package algorithm.mapmatching.hmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.MatcherIMPL;
import algorithm.pathfinder.astar.Astar;
import constants.Constants;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;
import entity.graph.AdjacencyGraph;
import entity.graph.Graph;
import utils.GaussianDistribution;

/*
 * @author pjl
 * @version 创建时间：2019年5月20日 下午8:27:36
 * 标准hmm
 */
public class HMM extends MatcherIMPL {
	protected static int maxInterval = 180000;// 3min
	protected GaussianDistribution gd;

	protected static double BETA = 5;
	protected static double MAX_RADIUS = 200;
	protected static double RADIUS = 20;
	protected static double LowProbabilityRoutes = 2000;
	/*
	 * 当前节点之前有多少个奇异点
	 */
	static double MIN_PROB = 0.0001;
	/*
	 * HMM匹配过程需要的参数
	 */
//	上一个状态
	public List<HMMNode> preState = new ArrayList<HMMNode>();
//	上一个pointFeatures的时间
	public Long preDatetime = null;
//	匹配链中的pointFeatures
	public List<Integer> matchingFeatureIndexs = new ArrayList<Integer>();
//	当前pointFeatures的时间
	Long datetime = null;

	public HMM(SpatialIndex index) {
		super(index);
		gd = new GaussianDistribution(Constants.MU, Constants.SIGMA);
	}

	public HMM(SpatialIndex index, boolean debug) {
		super(index, debug);
		gd = new GaussianDistribution(Constants.MU, Constants.SIGMA);
	}

	@Override
	public void match(int start, int end) {
		for (int i = start; i < end; ++i)
			matchFeature(i);
		saveData();
	}

	@Override
	public void matchFeature(int i) {
		if (debug) {
			System.out.println("--------------------------" + i);
		}
		datetime = origin.get(i).getTime();
		// 判定重置马尔科夫链(两点时间差大于设定值)
		if (preDatetime != null && datetime - preDatetime > maxInterval) {
			saveData();
			preDatetime = null;
		}
		List<HMMNode> nextState = getNextState(origin.get(i));
		if (debug) {
			System.out.println("thisState size : " + nextState.size());
		}
		// 该点有效，覆盖之前的状态
		if (nextState.size() > 0) {
			matchingFeatureIndexs.add(i);
			preDatetime = datetime;
			preState = nextState;
		} else {// 否则gap++，不覆盖上一个状态
			String osm_id = null;
			PointFeature temp = origin.get(i);
			Point p = temp.getPoint();
			Coordinate GPSCoordinate = p.getCoordinate();
			List<RoadSegment> lines = getPossibleRoads(GPSCoordinate);
			double minDist = Constants.MAX_SEARCH_DISTANCE + 10000;
			Coordinate minDistPoint = null;
			for (RoadSegment road : lines) {
				LocationIndexedLine line = new LocationIndexedLine(road.getGeom());
				LinearLocation here = line.project(GPSCoordinate);
				Coordinate point = line.extractPoint(here);
				double dist = point.distance(GPSCoordinate);
				if (dist < minDist) {
					minDist = dist;
					minDistPoint = point;
					osm_id = road.getID();
				}
			}
			if (minDistPoint == null) {
				minDistPoint = GPSCoordinate;
			}
			saveData(i, osm_id, geometryFactory.createPoint(minDistPoint));
		}
	}

	/*
	 * 将概率标准化（使列表中hmmnode的概率之和为1）
	 */
	static void normalize(List<HMMNode> nextState) {
		if (nextState != null && nextState.size() > 0) {
			double sum = 0;
			for (HMMNode h : nextState) {
				sum += h.prob;
			}
			Iterator<HMMNode> iterator = nextState.iterator();
			while (iterator.hasNext()) {
				HMMNode hmmNode = iterator.next();
				double prob = hmmNode.prob / sum;
				if (prob < MIN_PROB) {
					iterator.remove();
				} else {
					hmmNode.prob = prob;
				}
			}
		}

	}

	/*
	 * 计算下一个马尔科夫链的状态列表
	 */
	List<HMMNode> getNextState(PointFeature pointFeature) {
		Coordinate pCoordinate = pointFeature.getPoint().getCoordinate();
		List<HMMNode> nextStateMap = new ArrayList<HMMNode>();
//	提取该点可能道路
		List<RoadSegment> lines = getPossibleRoads(pCoordinate, LowProbabilityRoutes / 2);
		if (lines.size() > 0) {
			Graph graph = new AdjacencyGraph(lines);
			for (RoadSegment lineFeature : lines) {
				HMMNode hmmNode = getNextHMMNode(pointFeature, lineFeature, graph, pCoordinate);
				if (hmmNode != null)
					nextStateMap.add(hmmNode);
			}
		}
//		概率归一，防止过小
		normalize(nextStateMap);
		return nextStateMap;
	}

	HMMNode getNextHMMNode(PointFeature pointFeature, RoadSegment lineFeature, Graph graph, Coordinate pCoordinate) {
		LocationIndexedLine line = new LocationIndexedLine(lineFeature.getGeom());
//		备选道路上与当前点距离最近的点
		Coordinate closestCoordinate = line.extractPoint(line.project(pCoordinate));
//		计算各参数
		Map<String, Object> paramsMap = getParamsMap(lineFeature, graph, pCoordinate, closestCoordinate, pointFeature,
				line);
//		计算最终概率
		double prob = calcProb(paramsMap);
		if (debug) {
			String osmid = lineFeature.getID();
			System.out.println("road id:" + osmid + " prob:" + prob);
		}
		if (prob > 0) {
			HMMNode parentNode = ((TPData) paramsMap.get("tpData")).parentNode;
			return new HMMNode(prob, parentNode, closestCoordinate, lineFeature, pointFeature);
		}
		return null;
	}

//	由参数map计算最终权值
	protected double calcProb(Map<String, Object> paramsMap) {
		double ep = (double) paramsMap.get("ep");
		TPData tpData = (TPData) paramsMap.get("tpData");
		return ep * tpData.tp;
	}

	protected Map<String, Object> getParamsMap(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate, PointFeature pointFeature, LocationIndexedLine line) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		double distance = closestCoordinate.distance(pCoordinate);
		double ep = getEmissionProbility(distance);
		paramsMap.put("ep", ep);
		if (ep > 0) {
			if (debug) {
				System.out.println(" distance=" + distance + " ep=" + ep);
			}
			TPData tpData = getBestTP(lineFeature, graph, pCoordinate, closestCoordinate);
			paramsMap.put("tpData", tpData);
		} else {
			paramsMap.put("tpData", new TPData());
		}
		return paramsMap;
	}

	protected TPData getBestTP(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate) {
		if (preState.size() == 0) {
			return new TPData(1, null);
		}
		// 候选匹配点最近的两个节点
		Coordinate[] nowNodes = lineFeature.getClosestNodes(closestCoordinate);
		if (nowNodes != null) {
//			把当前点添加到图
			if (!graph.cutAndAdd(nowNodes[0], nowNodes[1], closestCoordinate)) {
				return new TPData();
			}
		}
		double maxtp = 0;
		HMMNode bestParent = null;
		for (HMMNode h : preState) {
			double temptp = getTransitionProbility(graph, closestCoordinate, h);
			if (debug) {
				String preosmid = h.road.getID();
				System.out.println("    road id:" + preosmid + " tp:" + temptp);
			}
			if (temptp > maxtp) {
				maxtp = temptp;
				bestParent = h;
			}
		}
		if (nowNodes != null) {
			graph.repareCut(nowNodes[0], nowNodes[1], closestCoordinate);
		}
		return new TPData(maxtp, bestParent);
	}

	/*
	 * tp的计算
	 */
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
		return getTransitionProbility(Math.abs(astar.routeDistance()), h.prepCoordinate.distance(closestCoordinate))
				* h.prob;
	}

	/*
	 * 求转移概率tp基础版
	 */
	protected double getTransitionProbility(double roadDistance, double distance) {
		if (debug) {
			System.out.println("  roadDistance:" + roadDistance + " distance:" + distance);
		}
		double dt = Math.abs(Math.abs(distance) - Math.abs(roadDistance));
		return dt > LowProbabilityRoutes ? 0 : (Math.pow(Math.E, -(dt / BETA))) / BETA;
	}

	/*
	 * 求某道路的可能概率 假设数据是服从高斯分布
	 */
	protected double getEmissionProbility(double distance) {
		if (distance < MAX_RADIUS) {
			return gd.probabilityDensity(distance);
		} else {
			return 0.0;
		}
	}
//	protected double getEmissionProbility(double distance) {
//		if (distance < MAX_RADIUS) {
//			return Math.pow(1.0 - (distance / MAX_RADIUS), 2);
//		} else {
//			return 0.0;
//		}
//	}
//	protected double getEmissionProbility(double distance) {
//		if (distance < MAX_RADIUS) {
//			double p1 = -0.5 * Math.pow((distance - Constants.MU) / Constants.SIGMA, 2);
//			return num * Math.pow(Math.E, p1);
//		} else {
//			return 0.0;
//		}
//	}
//
//	double num = 1 / (Math.sqrt(2 * Math.PI) * Constants.SIGMA);

	/*
	 * 从列表中找到概率最大的hmmnode
	 */
	protected static HMMNode getBest(List<HMMNode> stateMap) {
		HMMNode best = stateMap.get(0);
		for (int i = 1; i < stateMap.size(); ++i) {
			if (stateMap.get(i).prob > best.prob)
				best = stateMap.get(i);
		}
		return best;
	}

	/*
	 * 结果保存，重置马尔科夫链
	 */
	protected void saveData() {
		if (debug) {
			System.out.println("writeData,matchingFeaturesSize= " + matchingFeatureIndexs.size());
		}
		if (preState != null && preState.size() > 0) {
			HMMNode best = getBest(preState);
			// 回溯得到链
			Stack<HMMNode> stack = new Stack<HMMNode>();
			while (best != null) {
				stack.push(best);
				best = best.parent;
			}
			if (!stack.isEmpty()) {
				// 向数组写入数据
				for (int i = 0; i < matchingFeatureIndexs.size(); ++i) {
					HMMNode h = stack.pop();
					saveData(matchingFeatureIndexs.get(i), h.road.getID(), h.matchedCoor);
				}
			}

		}
		preState.clear();
		matchingFeatureIndexs.clear();
	}
}
